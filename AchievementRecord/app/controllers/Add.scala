package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.{Achievement, Auth, Student_Achievement}
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

  def postCompettition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val textBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    val ach_id = Achievement.create(textBody.get("achievement_name")(0), textBody.get("date")(0), "test", textBody.get("reward")(0), textBody.get("category")(0), 1)
    println(ach_id)
    Student_Achievement.create(loggedIn.username.value, ach_id)
//    Achievement.insertToStudentAchievement(loggedIn.username.value, ach_id)
    Ok("Got" + textBody.toString)

  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
