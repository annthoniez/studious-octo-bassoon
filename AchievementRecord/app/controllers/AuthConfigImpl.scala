package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import models.Auth._
import models._
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._

import scala.reflect._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pichai Sivawat on 3/28/2016.
  */
trait AuthConfigImpl extends AuthConfig {
  type Id = String
  type User = Account
  /**
    * A type that is defined by every action for authorization.
    * This sample uses the following trait:
    *
    * sealed trait Role
    * case object Staff extends Role
    * case object Student extends Role
    */
  type Authority = Seq[Auth]
  /**
    * A `ClassTag` is used to retrieve an id from the Cache API.
    * Use something like this:
    */
  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds: Int = 3600

  def resolveUser(username: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = Future.successful(Account.findByUsername(username))

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Main.home))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = Future.successful(Redirect(routes.Application.login()))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Application.login))

  def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("no permission"))
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    authority.contains(Auth.valueOf(user.role_id))
  }

}
