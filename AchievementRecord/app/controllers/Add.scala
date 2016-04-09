package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.Auth
import play.api.mvc.{AnyContent, Controller}
import views.html

/**
  * Created by Pichai Sivawat on 4/8/2016.
  */
trait Add extends Controller with Pjax with AuthElement with AuthConfigImpl  {

  def achievement = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add("test"))
  }

  def competition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_competition("competition"))
  }

  def postCompettition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val textBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    Ok("Got" + textBody.toString)

  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
