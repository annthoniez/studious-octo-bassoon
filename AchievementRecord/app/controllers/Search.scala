package controllers

import java.time.LocalDate

import jp.t2v.lab.play2.auth.AuthElement
import models._
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{AnyContent, Controller}
import views.html

/**
  * Created by Pichai Sivawat on 4/15/2016.
  */
trait Search extends Controller with Pjax with AuthElement with AuthConfigImpl {

  def index() = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff, Auth.Teacher)) { implicit request =>
    Ok(html.search("search"))
  }

  def search() = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val body: AnyContent = request.body
    val textBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    println(textBody)

    var result = Achievement
      .joins(Achievement.accRef)
      .joins(Achievement.orgRef)
      .joins(Achievement.teacher_accRef)
      .joins(Achievement.compRef)
      .joins(Achievement.certRef)
      .joins(Achievement.ambRef).findAll()

    if (textBody.get.isDefinedAt("students")) {
      result = result.filter(a =>
        a.accs
          .map(_.username)
          .exists(textBody.get("students").contains))
    }
    if (textBody.get.isDefinedAt("teachers")) {
      result = result.filter(a =>
        a.t_accs
          .map(_.username)
          .exists(textBody.get("teachers").contains))
    }
    if (textBody.get.isDefinedAt("orgs")) {
      result = result.filter(a =>
        a.orgs
          .map(_.id.toString)
          .exists(textBody.get("orgs").contains))
    }
    if (textBody.get.isDefinedAt("category")) {
      result = result.filter(a =>
        textBody.get("category").contains(a.category))
    }
    if (textBody.get("daterange").head != "") {
      val daterange = textBody.get("daterange").head.split(" to ").toSeq

      val startDate = LocalDate.parse(daterange.head)
      val endDate = LocalDate.parse(daterange.last)

      result = result.filter(a =>
        a.date.isBefore(endDate) && a.date.isAfter(startDate))
    }
    if (textBody.get.isDefinedAt("ach_type")) {
      result = result.filter(a =>
        textBody.get("ach_type").contains(a.achievement_type.toString))
      if (textBody.get("ach_type").length == 1 && textBody.get("ach_type").head == "1") {
        if (textBody.get.isDefinedAt("event_name")) {
          result = result.filter(a =>
            a.comp.get.event_name.contains(textBody.get("event_name").head))
        }
        if (textBody.get.isDefinedAt("rank")) {
          result = result.filter(a =>
            textBody.get("rank").contains(a.comp.get.rank))
        }
        if (textBody.get.isDefinedAt("level")) {
          result = result.filter(a =>
            textBody.get("level").contains(a.comp.get.level))
        }
      } else if (textBody.get("ach_type").length == 1 && textBody.get("ach_type").head == "2") {
        if (!textBody.get.isDefinedAt("show_exp")) {
          result = result.filter(a =>
            a.cert.get.exp_date.isAfter(LocalDate.now()))
        }
      }
    }

    result.foreach(println)

    val json = JsArray(result.map(r => Json.obj(
      "achievement_id" -> r.id,
      "achievement_name" -> r.achievement_name,
      "photo" -> r.photo,
      "date" -> r.date,
      "reward" -> r.reward,
      "category" -> r.category,
      "achievement_type" -> r.achievement_type,
      "teachers" -> JsArray(r.t_accs.map(t => Json.obj(
        "username" -> t.username,
        "th_prename" -> t.th_prename,
        "th_name" -> t.th_name))),
      "students" -> JsArray(r.accs.map(s => Json.obj(
        "username" -> s.username,
        "th_name" -> s.th_name))),
      "orgs" -> JsArray(r.orgs.map(o => Json.obj(
        "organization_name" -> o.organization_name))),
      "comp" -> Json.obj(
        "event_name" -> r.comp.map(_.event_name).getOrElse("").toString,
        "topic" -> r.comp.map(_.topic).getOrElse("").toString,
        "level" -> r.comp.map(_.level).getOrElse("").toString,
        "rank" -> r.comp.map(_.rank).getOrElse("").toString),
      "cert" -> Json.obj(
        "exp_date" -> r.cert.map(_.exp_date).getOrElse("").toString),
      "amb" -> Json.obj(
        "year" -> r.amb.map(_.year).getOrElse("").toString)
    )))
    Ok(json)
  }
  protected val main: User => Template = html.main.apply
}

object Search extends Search
