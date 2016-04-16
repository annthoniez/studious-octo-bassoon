package controllers

import java.io.File
import java.time.LocalDateTime
import java.util.UUID

import jp.t2v.lab.play2.auth.AuthElement
import models._
import play.api.libs.Crypto
import play.api.mvc.{AnyContent, Controller}
import views.html

/**
  * Created by Pichai Sivawat on 4/8/2016.
  */
trait Add extends Controller with Pjax with AuthElement with AuthConfigImpl  {

  def achievement = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add("test"))
  }

  def competition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_competition("competition"))
  }

  def cert = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_cert("cert"))
  }

  def amb = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_amb("amb"))
  }

  def postCompetition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody = multiPartBody.get.asFormUrlEncoded
    println(textBody)
    //println(multiPartBody.get.file("file"))
    var saveFileName = ""

    multiPartBody.get.file("file").map { p =>
      val contentType: Option[String] = p.contentType
      if (Seq("image/jpeg", "image/png").contains(contentType.get)) {
        println(contentType)
        saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)
        p.ref.moveTo(new File(play.Play.application().path().toString + s"/public/uploads/$saveFileName"))
      }
    }
    val student_ids: Set[String] = textBody.get("student_ids").head.toSet + loggedIn.username.value
    val teacher_names: Set[String] = textBody.get("teacher_names").head.toSet
    val orgs: Set[String] = textBody.get("orgs").head.toSet
    val rank = if (textBody.get("rank").head.head == "0") textBody.get("rank_des").head.head else textBody.get("rank").head.head

    val ach_id = Achievement.create(textBody.get("achievement_name").head.head, textBody.get("date").head.head, saveFileName, textBody.get("reward").head.head, textBody.get("category").head.head, 1)

    student_ids.foreach(s => if (s != "") Student_Achievement.create(s, ach_id))
    teacher_names.foreach(t => if (t != "") Teacher_Achievement.create(models.Teacher.getProfile(t).teacher_id.value, ach_id))
    orgs.foreach(o => if (o != "") Organization_Achievement.create(o.toLong, ach_id))

    Competition.create(textBody.get("event_name").head.head, textBody.get("topic").head.head, textBody.get("level").head.head, rank, ach_id)
    Ok("Got" + textBody)
  }

  def postCert = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody = multiPartBody.get.asFormUrlEncoded
    //println(multiPartBody.get.file("file"))
    var saveFileName = ""

    multiPartBody.get.file("file").map { p =>
      val contentType: Option[String] = p.contentType
      if (Seq("image/jpeg", "image/png").contains(contentType.get)) {
        println(contentType)
        saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)
        p.ref.moveTo(new File(play.Play.application().path().toString + s"/public/uploads/$saveFileName"))
      }
    }

    val ach_id = Achievement.create(textBody.get("achievement_name").head.head, textBody.get("date").head.head, saveFileName, "", "วิชาการ", 2)
    Student_Achievement.create(loggedIn.username.value, ach_id)
    Organization_Achievement.create(textBody.get("orgs").head.head.toLong, ach_id)

    Cert.create(textBody.get("exp_date").head.head, ach_id)
    Ok("Got" + textBody)
  }

  def portAmb = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody = multiPartBody.get.asFormUrlEncoded
    //println(multiPartBody.get.file("file"))
    var saveFileName = ""

    multiPartBody.get.file("file").map { p =>
      val contentType: Option[String] = p.contentType
      if (Seq("image/jpeg", "image/png").contains(contentType.get)) {
        println(contentType)
        saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)
        p.ref.moveTo(new File(play.Play.application().path().toString + s"/public/uploads/$saveFileName"))
      }
    }

    val ach_id = Achievement.create(textBody.get("achievement_name").head.head, textBody.get("date").head.head, saveFileName, "", "วิชาการ", 3)
    Student_Achievement.create(loggedIn.username.value, ach_id)
    Organization_Achievement.create(textBody.get("orgs").head.head.toLong, ach_id)

    Ambassador.create(textBody.get("year").head.head, ach_id)
    Ok("Got" + textBody)
  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
