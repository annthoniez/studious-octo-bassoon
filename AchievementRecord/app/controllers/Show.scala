package controllers

import java.time.LocalDate

import jp.t2v.lab.play2.auth._
import models._
import play.api.libs.json._
import play.api.mvc.Controller
import views.html

/**
  * Created by Pichai Sivawat on 4/7/2016.
  */
trait Show extends Controller with Pjax with AuthElement with AuthConfigImpl {

  def achievement(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val ach = Achievement.getAchWithChild(id)

    if (loggedIn.role_id == 3 &&
      !Achievement.joins(Achievement.accRef).findById(id)
        .get.accs.map(_.username)
        .contains(loggedIn.username.value)) {
      Ok(html.tarwised.Forb("Forbidden"))
    } else if (loggedIn.role_id == 1 &&
      !Achievement.joins(Achievement.teacher_accRef).findById(id)
        .get.t_accs.map(_.username)
        .contains(loggedIn.username.value)) {
      Ok(html.tarwised.Forb("Forbidden"))
    } else {
      val s_accs = Achievement.joins(Achievement.accRef).findById(id).map(_.accs)
      val t_accs = Achievement.joins(Achievement.teacher_accRef).findById(id).map(_.t_accs)
      val orgs = Achievement.joins(Achievement.orgRef).findById(id).map(_.orgs)

      var canEdit: Boolean = false
      if (loggedIn.role_id == 2) {
        canEdit = true
      } else if (loggedIn.role_id == 3) {
        println(ach)
        canEdit = s_accs.get.map(_.username).contains(loggedIn.username.value) && ach.get.created_at.isAfter(LocalDate.now().minusDays(3))
      }

      val photo = Json.parse(ach.get.photo).as[List[String]]
      println("photos" + photo)

      Ok(html.achievement("ผลงาน - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", ach, s_accs, t_accs, orgs, canEdit, photo))
    }
  }

  def profile(username: String) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    if ((loggedIn.role_id == 3 && loggedIn.username.value != username) || (loggedIn.role_id == 1 && Account.findByUsername(username).get.role_id == 2)) {
      Ok(html.tarwised.Forb("Forbidden"))
    } else {
      val role_id = models.Account.findByUsername(username).head.role_id
      val th_name = Auth.valueOf(role_id) match {
        case Auth.Student => models.Student.getProfile(username).th_name
        case Auth.Staff => models.Staff.getProfile(username).th_name
        case Auth.Teacher => models.Teacher.getProfile(username).th_prename + models.Teacher.getProfile(username).th_name
      }
      val id = Auth.valueOf(role_id) match {
        case Auth.Student => models.Student.getProfile(username).student_id
        case Auth.Staff => models.Staff.getProfile(username).staff_id
        case Auth.Teacher => models.Teacher.getProfile(username).teacher_id
      }

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
      val achs2 = achs.map(a => Achievement.getAchWithChild(a.id)).reverse
      val s_accs = Achievement.getStudentInAch(achs)
      val t_accs = Achievement.getTeacherInAch(achs)
      val orgs = Achievement.getOrgInAch(achs)

      println(profile)

      val photos = achs2.map(a => Json.parse(a.get.photo).as[List[String]])
      println(photos)

      Ok(html.profile("โปรไฟล์ - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", profile, achs2, s_accs, t_accs, orgs, th_name, id.value, photos, loggedIn))
    }
  }

  def jsonUser(text: String) = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    val teachers = models.Teacher.findAll().filter(_.th_name.contains(text)).map(t =>
      Json.obj(
        "value" -> JsString(t.username),
        "label" -> JsString(t.th_prename + t.th_name)))
    val students = models.Student.findAll().filter(_.th_name.contains(text)).map(s =>
      Json.obj(
        "value" -> JsString(s.username),
        "label" -> JsString(s.th_name)))
    val all = teachers ++ students
    Ok(JsArray(all))
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
