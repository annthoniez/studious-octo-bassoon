package controllers

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
    val textBody = body.asFormUrlEncoded
    println(textBody)
    var result = Achievement.joins(Achievement.accRef).joins(Achievement.orgRef).joins(Achievement.teacher_accRef).joins(Achievement.compRef).joins(Achievement.certRef).joins(Achievement.ambRef).findAll()
    result = result.filter(a => a.accs.map(_.username).exists(textBody.get("students").contains))
    result.foreach(println)
    val json = JsArray(result.map(r => Json.obj(
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
        "event_name" -> r.comp.get.event_name,
        "topic" -> r.comp.get.topic,
        "level" -> r.comp.get.level,
        "rank" -> r.comp.get.rank),
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