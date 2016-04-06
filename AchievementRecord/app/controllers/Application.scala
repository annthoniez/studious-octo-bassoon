package controllers

import play.api.libs.json._
import play.api.mvc._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import jp.t2v.lab.play2.auth._
import models.{Account, Username}
import views.html

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller with LoginLogout with AuthConfigImpl {

//  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
//  }
//  def users = Action.async {
//    UserService.listAllUsers map {users =>
//      Ok(views.html.index("aaaa", users)) }
//  }

  val loginForm = Form {
    mapping("username" -> text, "password" -> text)(Account.authenticate)(_.map(u => (u.username.value, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
    * Return the `gotoLogoutSucceeded` method's result in the logout action.
    *
    * Since the `gotoLogoutSucceeded` returns `Future[Result]`,
    * you can add a procedure like the following.
    *
    *   gotoLogoutSucceeded.map(_.flashing(
    *     "success" -> "You've been logged out"
    *   ))
    */
  def logout = Action.async { implicit request =>
    // do something...
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ).removingFromSession("rememberme"))
  }

  /**
    * Return the `gotoLoginSucceeded` method's result in the login action.
    *
    * Since the `gotoLoginSucceeded` returns `Future[Result]`,
    * you can add a procedure like the `gotoLogoutSucceeded`.
    */
  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.login(formWithErrors))),
      user => gotoLoginSucceeded(user.get.username.value)
    )
  }
}
