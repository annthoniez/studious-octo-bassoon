package controllers

import akka.actor.FSM.->
import jp.t2v.lab.play2.auth.AuthElement
import models._
import play.api.mvc.{AnyContent, Controller}
import views.html

/**
  * Created by Pichai Sivawat on 4/21/2016.
  */
trait Setting extends Controller with Pjax with AuthElement with AuthConfigImpl {
  def index = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff, Auth.Teacher)) { implicit request =>
    Ok(html.setting("ตั้งค่า - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา"))
  }

  def changePassword = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff, Auth.Teacher)) { implicit request =>
    val body: AnyContent = request.body
    val textBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded
    println(textBody)

    val password: String = textBody.get("password").head
    val password_rep: String = textBody.get("password_rep").head

    if (password == password_rep) {
      Account.updateById(loggedIn.username).withAttributes(
        'password -> password
      )
      Redirect(routes.Setting.index).flashing("result" -> "เปลี่ยนรหัสผ่านเรียบร้อย")
    } else {
      Redirect(routes.Main.tarwised)
    }


  }

  protected val main: User => Template = html.main.apply
}

object Setting extends Setting
