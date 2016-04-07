package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.{Achievement, Auth}
import play.api.mvc.Controller
import views.html

/**
  * Created by Pichai Sivawat on 4/7/2016.
  */
trait Show extends Controller with Pjax with AuthElement with AuthConfigImpl {

  def profile(username: String) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    val role_id = models.Account.findByUsername(username).head.role_id
    val profile = Auth.valueOf(role_id) match {
      case Auth.Student => models.Student.joins(models.Student.curriRef).joins(models.Student.trackRef).findAll().filter(_.student_id.value == username).head
      case Auth.Staff => models.Staff.joins(models.Staff.sectionRef).findAll().filter(_.username == username).head
      case Auth.Teacher => models.Teacher.joins(models.Teacher.statRef).findAll().filter(_.username == username).head
    }
    val achs = Auth.valueOf(role_id) match {
      case Auth.Student => models.Student.joins(models.Student.achRef).findAll().filter(_.username == username).head.achs
      case Auth.Teacher => models.Teacher.joins(models.Teacher.achRef).findAll().filter(_.username == username).head.achs
      case Auth.Staff => Achievement.findAll() //TODO Don't show on Staff
    }

    val s_accs = Achievement.joins(Achievement.accRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.accs)
    val t_accs = Achievement.joins(Achievement.teacher_accRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.t_accs)
    Ok(html.profile("Profile", profile, achs, s_accs, t_accs))
  }

  protected val main: User => Template = html.main.apply
}

object Show extends Show
