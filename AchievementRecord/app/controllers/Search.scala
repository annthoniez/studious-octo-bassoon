package controllers

import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc.Controller
import views.html
import models._

/**
  * Created by Pichai Sivawat on 4/15/2016.
  */
trait Search extends Controller with Pjax with AuthElement with AuthConfigImpl {

  def search() = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff, Auth.Teacher)) { implicit request =>
    Ok(html.search("search"))
  }
  protected val main: User => Template = html.main.apply
}

object Search extends Search
