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
    val achs = Auth.valueOf(loggedIn.role_id) match {
      case Auth.Student => models.Student.getAchs(loggedIn.username.value)
      case Auth.Teacher => models.Teacher.getAchs(loggedIn.username.value)
      case Auth.Staff => Achievement.findAll()
    }
    val s_accs = Achievement.getStudentInAch(achs)
    val t_accs = Achievement.getTeacherInAch(achs)
    Ok(html.home("Home", achs, s_accs, t_accs))
  }

  protected val main: User => Template = html.main.apply

}

object Main extends Main
