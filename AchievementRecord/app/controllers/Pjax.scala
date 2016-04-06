package controllers

import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.StackableController
import jp.t2v.lab.play2.stackc.{RequestAttributeKey, RequestWithAttributes}
import play.api.mvc.{Controller, Result}
import play.twirl.api.Html
import views.html

import scala.concurrent.Future
/**
  * Created by Pichai Sivawat on 3/29/2016.
  */
trait Pjax extends StackableController with AuthElement {
  self: Controller with AuthConfigImpl =>

  type Template = String => Html => Html

  case object TemplateKey extends RequestAttributeKey[Template]

  abstract override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = {
    super.proceed(req) { req =>
      val template: Template = if (req.headers.keys("X-Pjax")) html.pjaxTemplate.apply else main(loggedIn(req))
      f(req.set(TemplateKey, template))
    }
  }

  implicit def template(implicit req: RequestWithAttributes[_]): Template = req.get(TemplateKey).get

  protected val main: User => Template

}
