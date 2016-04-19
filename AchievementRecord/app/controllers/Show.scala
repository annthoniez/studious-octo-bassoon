package controllers

import java.time.LocalDate

import jp.t2v.lab.play2.auth.AuthElement
import models.{Achievement, Auth, Competition, Organization}
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
    val orgs = Achievement.joins(Achievement.orgRef).findById(id).map(_.orgs)

    val canEdit = s_accs.get.map(_.username).contains(loggedIn.username.value) && ach.get.created_at.isAfter(LocalDate.now().minusDays(3))

    Ok(html.achievement("achievement", ach, s_accs, t_accs, orgs, canEdit))
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

  def jsonAll(id: Int) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val teachers = models.Teacher.findAll().map(t =>
      Json.obj(
        "value" -> JsString(t.username),
        "label" -> JsString(t.th_name)))
    val students = models.Student.findAll().map(s =>
      Json.obj(
        "value" -> JsString(s.username),
        "label" -> JsString(s.th_name)))
    val staffs = models.Staff.findAll().map(s =>
      Json.obj(
        "value" -> JsString(s.username),
        "label" -> JsString(s.th_name)))
    val all = teachers ++ students ++ staffs
    val json: JsValue = id match {
      case 0 => JsArray(all)
      case 1 => JsObject(models.Student.findAll().map(s => s.username -> JsString(s.th_name)))
      case 2 => JsArray(teachers)
      case 3 => JsArray(Organization.findAll().map(o => Json.obj("value" -> o.id, "text" -> o.organization_name)))
      case 4 => JsArray(models.Student.findAll().map(s => Json.obj("value" -> JsString(s.username), "text" -> JsString(s.th_name))))
      case 5 => JsArray(models.Teacher.findAll().map(t => Json.obj("value" -> JsString(t.username), "text" -> JsString(t.th_name))))
      case 6 => JsArray(Competition.findAll().map(_.rank).toSet[String].toSeq.map(c => Json.obj("value" -> JsString(c), "text" -> JsString(c))))
    }
    Ok(json)
  }

  protected val main: User => Template = html.main.apply
}

object Show extends Show
