package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.{Achievement, Auth}
import play.api.mvc.Controller
import views.html

/**
  * Created by Pichai Sivawat on 4/17/2016.
  */
trait Edit extends Controller with Pjax with AuthElement with AuthConfigImpl {
  def edit(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val ach = Achievement
      .joins(Achievement.accRef)
      .joins(Achievement.orgRef)
      .joins(Achievement.teacher_accRef)
      .joins(Achievement.compRef)
      .joins(Achievement.certRef)
      .joins(Achievement.ambRef)
      .findById(id)

    ach.get.achievement_type match {
      case 1 => Ok(html.add_competition("competition", ach, loggedIn))
      case 2 => Ok(html.add_cert("cert", ach, loggedIn))
      case 3 => Ok(html.add_amb("amb", ach, loggedIn))
    }

  }

  protected val main: User => Template = html.main.apply
}


object Edit extends Edit
