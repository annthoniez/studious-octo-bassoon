package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.Auth
import play.api.mvc.Controller
import views.html

/**
  * Created by Pichai Sivawat on 4/8/2016.
  */
trait Add extends Controller with Pjax with AuthElement with AuthConfigImpl  {

  def achievement = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add("test"))
  }

  def competition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_ach("competition"))
  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
