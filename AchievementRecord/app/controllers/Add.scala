package controllers

import java.io.File
import java.time.LocalDateTime
import java.util.UUID
import javax.ws.rs.core.MediaType

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import com.sun.jersey.core.util.MultivaluedMapImpl
import jp.t2v.lab.play2.auth._
import models._
import play.api.libs.Crypto
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{AnyContent, Controller}
import views.html
import views.html.helper.form

import scala.collection.mutable.ListBuffer

/**
  * Created by Pichai Sivawat on 4/8/2016.
  */
trait Add extends Controller with Pjax with AuthElement with AuthConfigImpl {
  def competition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_competition("เพิ่มการแข่งขัน - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", None, loggedIn))
  }

  def cert = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_cert("เพิ่มใบรับรอง - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", None, loggedIn))
  }

  def amb = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    Ok(html.add_amb("เพิ่มตัวแทนองค์กร - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", None, loggedIn))
  }

  def isOrgExists(name: String): Boolean = {
    if (name.forall(_.isDigit)) {
      val org: Option[Organization] = Organization.findById(name.toLong)
      org.isDefined
    } else {
      val org: Seq[Organization] = Organization.findAll().filter(o => o.organization_name == name)
      org.nonEmpty
    }
  }

  def postCompetition = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody = multiPartBody.get.asFormUrlEncoded
    println(textBody)
    println("multipart: " + multiPartBody.get.files)
    var saveFileName = ""
    var filenames: ListBuffer[String] = ListBuffer();

    multiPartBody.get.files.map { p =>
      val contentType: Option[String] = p.contentType
      if (Seq("image/jpeg", "image/png").contains(contentType.get)) {
        println(contentType)
        println(p.filename)
        saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)
        filenames += saveFileName
        p.ref.moveTo(new File(play.Play.application().path().getCanonicalPath() + "/../webapps/" + s"/public/uploads/$saveFileName"))
      }
    }

    println(filenames)
    val jsonFilenames = Json.toJson(filenames)
    println(jsonFilenames.toString())

    val student_ids: Set[String] = textBody.get("student_ids").head.toSet + loggedIn.username.value
    val teacher_names: Set[String] = textBody.get("teacher_names").head.toSet
    //val orgs: Set[String] = textBody.get("orgs").head.toSet
    val orgs: Set[String] = textBody.get("orgs").head.toSet[String].map(o => if(!isOrgExists(o)) Organization.create(o).toString else o)
    println(orgs)
    val rank = if (textBody.get("rank").head.head == "0") textBody.get("rank_des").head.head else textBody.get("rank").head.head

    val ach_id = Achievement.create(
      textBody.get("achievement_name").head.head,
      textBody.get("date").head.head,
      jsonFilenames.toString(),
      textBody.get("reward").head.head,
      textBody.get("category").head.head,
      1)

    student_ids.foreach(s =>
      if (s != "")
        Student_Achievement.create(s, ach_id))

    teacher_names.foreach(t =>
      if (t != "")
        Teacher_Achievement.create(models.Teacher.getProfile(t).teacher_id.value, ach_id))

    orgs.foreach(o =>
      if (o != "")
        Organization_Achievement.create(o.toLong, ach_id))

    Competition.create(
      textBody.get("event_name").head.head,
      textBody.get("topic").head.head,
      textBody.get("level").head.head,
      rank,
      ach_id)

    sendMail("Add ach noti", routes.Show.achievement(ach_id).absoluteURL())
    Redirect(routes.Show.achievement(ach_id))
  }

  def postCert = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody = multiPartBody.get.asFormUrlEncoded
    //println(multiPartBody.get.file("file"))
    var saveFileName = ""
    var filenames: ListBuffer[String] = ListBuffer();

    multiPartBody.get.files.map { p =>
      val contentType: Option[String] = p.contentType
      if (Seq("image/jpeg", "image/png").contains(contentType.get)) {
        println(contentType)
        saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)
        filenames += saveFileName
        p.ref.moveTo(new File(play.Play.application().path().getCanonicalPath() + "/../webapps/" + s"/public/uploads/$saveFileName"))
      }
    }

    val jsonFilenames = Json.toJson(filenames)

    val ach_id = Achievement.create(
      textBody.get("achievement_name").head.head,
      textBody.get("date").head.head,
      jsonFilenames.toString(),
      "",
      "วิชาการ",
      2)

    val o: String = textBody.get("orgs").head.head.toString
    val org_id: Long = if(!isOrgExists(o)) Organization.create(o) else o.toLong
    Student_Achievement.create(loggedIn.username.value, ach_id)
    Organization_Achievement.create(org_id, ach_id)

    Cert.create(textBody.get("exp_date").head.head, ach_id)
    Redirect(routes.Show.achievement(ach_id))
  }

  def portAmb = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody = multiPartBody.get.asFormUrlEncoded
    //println(multiPartBody.get.file("file"))
    var saveFileName = ""
    var filenames: ListBuffer[String] = ListBuffer();

    multiPartBody.get.files.map { p =>
      val contentType: Option[String] = p.contentType
      if (Seq("image/jpeg", "image/png").contains(contentType.get)) {
        println(contentType)
        saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)
        filenames += saveFileName
        p.ref.moveTo(new File(play.Play.application().path().getCanonicalPath() + "/../webapps/" + s"/public/uploads/$saveFileName"))
//        p.ref.moveTo(new File(play.Play.application().path().toString + s"/public/uploads/$saveFileName"))
      }
    }

    val jsonFilenames = Json.toJson(filenames)

    val ach_id = Achievement.create(
      textBody.get("achievement_name").head.head,
      textBody.get("date").head.head,
      jsonFilenames.toString(),
      "",
      "วิชาการ",
      3)

    val o: String = textBody.get("orgs").head.head.toString
    val org_id: Long = if(!isOrgExists(o)) Organization.create(o) else o.toLong

    Student_Achievement.create(loggedIn.username.value, ach_id)
    Organization_Achievement.create(org_id, ach_id)

    Ambassador.create(textBody.get("year").head.head, ach_id)
    sendMail("Add ach noti", routes.Show.achievement(ach_id).absoluteURL())
    Redirect(routes.Show.achievement(ach_id))
  }

  def sendMail(title: String, url: String) = {
    val mailLst: Seq[String] = models.Staff.where('noti -> 1).apply().map(_.email)
    val recipientVar = Json.toJson(mailLst.map(m => m -> Map("uid" -> UUID.randomUUID().toString.slice(0, 8))).toMap)
    println(recipientVar)

    val client = Client.create()
    client.addFilter(new HTTPBasicAuthFilter("api", "key-ca55036df0415f832d094da420707beb"))
    val webResource = client.resource("https://api.mailgun.net/v3/sandbox72112086319e44f5962246e6fe1ddecb.mailgun.org/messages")

    val form = new MultivaluedMapImpl
    form.add("from", "System <postmaster@sandbox72112086319e44f5962246e6fe1ddecb.mailgun.org>")
    mailLst.foreach(m => form.add("to", m))
    form.add("recipient-variables", recipientVar)
    form.add("subject", title)
    form.add("html", url)

    println(form)

    webResource.`type`(MediaType.APPLICATION_FORM_URLENCODED).post(form)

    Ok("test")
  }

  protected val main: User => Template = html.main.apply
}


object Add extends Add
