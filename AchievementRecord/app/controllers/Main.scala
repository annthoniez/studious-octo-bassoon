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
//    val achs = Achievement.joins(Achievement.accRef).findAll()
    val achs = models.Student.joins(models.Student.achRef).findAll().filter(_.username == loggedIn.username.value).head.achs
    val accs = Achievement.joins(Achievement.accRef).findAll().filter(_.accs.map(_.username).contains(loggedIn.username.value)).map(_.accs)
    Ok(html.home("Home", achs, accs))
  }

  def profile = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Teacher, Auth.Staff)) { implicit request =>
    //val account = Account.joins(Account.stuRef).findAll().filter(_.username == loggedIn.username).head.stu
    val profile = Auth.valueOf(loggedIn.role_id) match {
      case Auth.Student => models.Student.joins(models.Student.curriRef).joins(models.Student.trackRef).findAll().filter(_.student_id == loggedIn.username).head
      case Auth.Staff => models.Staff.joins(models.Staff.sectionRef).findAll().filter(_.username == loggedIn.username.value).head
      case Auth.Teacher => models.Teacher.joins(models.Teacher.statRef).findAll().filter(_.username == loggedIn.username.value).head
    }
    Ok(html.profile("Profile", profile))
  }

  protected val main: User => Template = html.main.apply

}

object Main extends Main
