package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models._
import play.api.mvc._
import views.html

/**
  * Created by Pichai Sivawat on 3/29/2016.
  */
trait Main extends Controller with Pjax with AuthElement with AuthConfigImpl {
  def home = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val username = loggedIn.username.value
    val th_name = Auth.valueOf(loggedIn.role_id) match {
      case Auth.Student => models.Student.getProfile(username).th_name
      case Auth.Staff => models.Staff.getProfile(username).th_name
      case Auth.Teacher => models.Teacher.getProfile(username).th_name
    }
    val id = Auth.valueOf(loggedIn.role_id) match {
      case Auth.Student => models.Student.getProfile(username).student_id
      case Auth.Staff => models.Staff.getProfile(username).staff_id
      case Auth.Teacher => models.Teacher.getProfile(username).teacher_id
    }
    val achs = Auth.valueOf(loggedIn.role_id) match {
      case Auth.Student => models.Student.getAchs(loggedIn.username.value)
      case Auth.Teacher => models.Teacher.getAchs(loggedIn.username.value)
      case Auth.Staff => Achievement.findAll()
    }
    val achs2 = achs.map(a => Achievement.getAchWithChild(a.id)).reverse
    val s_accs = Achievement.getStudentInAch(achs)
    val t_accs = Achievement.getTeacherInAch(achs)
    val orgs = Achievement.getOrgInAch(achs)
    Ok(html.home("ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", achs2, s_accs, t_accs, orgs, th_name, id.value, loggedIn))
  }

  def tarwised = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff, Auth.Teacher)) { implicit request =>
    Ok(html.tarwised.Forb("Forbidden"))
  }

  protected val main: User => Template = html.main.apply

}

object Main extends Main
