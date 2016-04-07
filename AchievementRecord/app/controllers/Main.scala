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
      case Auth.Student => models.Student.joins(models.Student.achRef).findAll().filter(_.username == loggedIn.username.value).head.achs
      case Auth.Teacher => models.Teacher.joins(models.Teacher.achRef).findAll().filter(_.username == loggedIn.username.value).head.achs
      case Auth.Staff => Achievement.findAll()
    }
    val s_accs = Achievement.joins(Achievement.accRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.accs)
    val t_accs = Achievement.joins(Achievement.teacher_accRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.t_accs)
//    val accs = Achievement.joins(Achievement.accRef).findAll().filter(_.accs.map(_.username).contains(loggedIn.username.value)).map(_.accs)
//    val t_accs = Achievement.joins(Achievement.teacher_accRef).findAll().filter(_.t_accs.map(_.username).contains(loggedIn.username.value)).map(_.t_accs)
    Ok(html.home("Home", achs, s_accs, t_accs))
  }

  protected val main: User => Template = html.main.apply

}

object Main extends Main
