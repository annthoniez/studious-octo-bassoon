package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models._
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

  def postCompetition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val textBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    val student_ids: Set[String] = textBody.get("student_ids").toSet + loggedIn.username.value
    val teacher_names: Set[String] = textBody.get("teacher_names").toSet
    val orgs: Set[String] = textBody.get("orgs").toSet
    val rank = if (textBody.get("rank").head == "0") textBody.get("rank_des").head else textBody.get("rank").head

    val ach_id = Achievement.create(textBody.get("achievement_name").head, textBody.get("date").head, "test", textBody.get("reward").head, textBody.get("category").head, 1)

    student_ids.foreach(s => if (s != "") Student_Achievement.create(s, ach_id))
    teacher_names.foreach(t => if (t != "") Teacher_Achievement.create(models.Teacher.getProfile(t).teacher_id.value, ach_id))
    orgs.foreach(o => if (o != "") Organization_Achievement.create(o.toLong, ach_id))

    Competition.create(textBody.get("event_name").head, textBody.get("topic").head, textBody.get("level").head, rank, ach_id)
    Ok("Got" + textBody.toString)
  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
