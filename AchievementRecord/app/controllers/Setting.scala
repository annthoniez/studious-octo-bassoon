package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models._
import play.api.mvc.Controller
import views.html

/**
  * Created by Pichai Sivawat on 4/21/2016.
  */
trait Setting extends Controller with Pjax with AuthElement with AuthConfigImpl {
  def index = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff, Auth.Teacher)) { implicit request =>
    Ok(html.setting("ตั้งค่า - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา"))
  }

  protected val main: User => Template = html.main.apply
}

object Setting extends Setting
