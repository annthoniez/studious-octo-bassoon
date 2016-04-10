package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.{Achievement, Auth}
import play.api.libs.json._
import play.api.mvc.Controller
import views.html

/**
  * Created by Pichai Sivawat on 4/7/2016.
  */
trait Show extends Controller with Pjax with AuthElement with AuthConfigImpl {

  def achievement(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val ach = Achievement.getAchWithChild(id)
    val s_accs = Achievement.joins(Achievement.accRef).findById(id).map(_.accs)
    val t_accs = Achievement.joins(Achievement.teacher_accRef).findById(id).map(_.t_accs)
    Ok(html.achievement("achievement", ach, s_accs, t_accs))
  }

  def profile(username: String) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val role_id = models.Account.findByUsername(username).head.role_id
    val profile = Auth.valueOf(role_id) match {
      case Auth.Student => models.Student.getProfile(username)
      case Auth.Staff => models.Staff.getProfile(username)
      case Auth.Teacher => models.Teacher.getProfile(username)
    }
    val achs = Auth.valueOf(role_id) match {
      case Auth.Student => models.Student.getAchs(username)
      case Auth.Teacher => models.Teacher.getAchs(username)
      case Auth.Staff => Achievement.findAll().filter(a => false)
    }

    val s_accs = Achievement.getStudentInAch(achs)
    val t_accs = Achievement.getTeacherInAch(achs)
    Ok(html.profile("Profile", profile, achs, s_accs, t_accs))
  }

  def jsonProfile = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val json: JsValue = Json.obj(
      "username" -> loggedIn.username.value,
      "th_name" -> models.Student.getProfile(loggedIn.username.value).th_name
    )
    Ok(json)
  }

  protected val main: User => Template = html.main.apply
}

object Show extends Show
