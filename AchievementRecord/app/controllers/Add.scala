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
    val ach_id = Achievement.create(textBody.get("achievement_name").head, textBody.get("date").head, "test", textBody.get("reward").head, textBody.get("category").head, 1)
    println(ach_id)
    Student_Achievement.create(loggedIn.username.value, ach_id)
    textBody.get("student_ids").foreach(s => Student_Achievement.create(s, ach_id))
    Ok("Got" + textBody.toString)

  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
