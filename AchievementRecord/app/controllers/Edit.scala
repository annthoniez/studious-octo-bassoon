package controllers

import java.time.LocalDate

import jp.t2v.lab.play2.auth.AuthElement
import models._
import play.api.mvc.{AnyContent, Controller}
import scalikejdbc._
import views.html

/**
  * Created by Pichai Sivawat on 4/17/2016.
  */
trait Edit extends Controller with Pjax with AuthElement with AuthConfigImpl {
  def edit(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student, Auth.Staff)) { implicit request =>
    val ach = Achievement
      .joins(Achievement.accRef)
      .joins(Achievement.orgRef)
      .joins(Achievement.teacher_accRef)
      .joins(Achievement.compRef)
      .joins(Achievement.certRef)
      .joins(Achievement.ambRef)
      .findById(id)

    var canEdit: Boolean = false
    if (loggedIn.role_id == 2) {
      canEdit = true
    } else {
      canEdit = ach.get.accs.map(_.username).contains(loggedIn.username.value) && ach.get.created_at.isAfter(LocalDate.now().minusDays(3))
    }

    if (!canEdit) {
      Ok(html.tarwised.Forb("Forbidden"))
    } else {
      ach.get.achievement_type match {
        case 1 => Ok(html.add_competition("แก้ไขการแข่งขัน - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", ach, loggedIn))
        case 2 => Ok(html.add_cert("แก้ไขใบรับรอง - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", ach, loggedIn))
        case 3 => Ok(html.add_amb("แก้ไขตัวแทนองค์กร - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา", ach, loggedIn))
      }
    }

  }

  def editStudents(id: Long, textBody: Map[String, Seq[String]], ach: Option[Achievement], loggedIn: Account) = {
    val curStu: Set[String] = ach.get.accs.map(_.username).toSet[String] - loggedIn.username.value
    val updateStu: Set[String] = textBody.get("student_ids").head.toSet[String]

    val delStu: Set[String] = curStu -- updateStu
    val addStu: Set[String] = updateStu -- curStu

    delStu.foreach(s =>
      if (s != "")
        Student_Achievement.deleteBy(
          sqls.eq(Student_Achievement.column.student_id, s)
            .and.eq(Student_Achievement.column.achievement_id, id)))
    addStu.foreach(s =>
      if (s != "")
        Student_Achievement.create(s, id))
  }

  def editTeachers(id: Long, textBody: Map[String, Seq[String]], ach: Option[Achievement], loggedIn: Account) = {
    val curTec: Set[String] = ach.get.t_accs.map(_.username).toSet[String]
    val updateTec: Set[String] = textBody.get("teacher_names").head.toSet[String]

    val delTec: Set[String] = curTec -- updateTec
    val addTec: Set[String] = updateTec -- curTec

    delTec.foreach(t =>
      if (t != "")
        Teacher_Achievement.deleteBy(
          sqls.eq(Teacher_Achievement.column.teacher_id, models.Teacher.getProfile(t).teacher_id.value)
            .and.eq(Teacher_Achievement.column.achievement_id, id)))
    addTec.foreach(t =>
      if (t != "")
        Teacher_Achievement.create(models.Teacher.getProfile(t).teacher_id.value, id))
  }

  def editOrgs(id: Long, textBody: Map[String, Seq[String]], ach: Option[Achievement], loggedIn: Account) = {
    val curOrg: Set[String] = ach.get.orgs.map(_.id.toString).toSet[String]
    val updateOrg: Set[String] = textBody.get("orgs").head.toSet[String].map(o => if(!o.forall(_.isDigit)) Organization.create(o).toString else o)

    val delOrg: Set[String] = curOrg -- updateOrg
    val addOrg: Set[String] = updateOrg -- curOrg

    delOrg.foreach(o =>
      if (o != "")
        Organization_Achievement.deleteBy(
          sqls.eq(Organization_Achievement.column.organization_id, o)
            .and.eq(Organization_Achievement.column.achievement_id, id)))
    addOrg.foreach(o =>
      if (o != "")
        Organization_Achievement.create(o.toLong, id))
  }

  def editCompetition(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multiPartBody = body.asMultipartFormData
    val textBody: Map[String, Seq[String]] = multiPartBody.get.asFormUrlEncoded
    println(textBody)

    //TODO update image

    val ach = Achievement
      .joins(Achievement.accRef)
      .joins(Achievement.teacher_accRef)
      .joins(Achievement.orgRef)
      .joins(Achievement.compRef).findById(id)

    val rank = if (textBody.get("rank").head.head == "0") textBody.get("rank_des").head.head else textBody.get("rank").head.head

    editStudents(id, textBody, ach, loggedIn)
    editTeachers(id, textBody, ach, loggedIn)
    editOrgs(id, textBody, ach , loggedIn)

    Achievement.updateById(id)
      .withAttributes(
        'achievement_name -> textBody.get("achievement_name").head.head,
        'date -> textBody.get("date").head.head,
        'reward -> textBody.get("reward").head.head,
        'category -> textBody.get("category").head.head)

    Competition.updateById(ach.get.comp.get.id)
      .withAttributes(
        'event_name -> textBody.get("event_name").head.head,
        'topic -> textBody.get("topic").head.head,
        'level -> textBody.get("level").head.head,
        'rank -> rank)
    Add.sendMail("Edit ach noti", routes.Show.achievement(id).absoluteURL())
    Redirect(routes.Show.achievement(id))
  }

  def editCert(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multipartBody = body.asMultipartFormData
    val textBody: Map[String, Seq[String]] = multipartBody.get.asFormUrlEncoded
    println(textBody)

    val ach = Achievement
      .joins(Achievement.orgRef)
      .joins(Achievement.certRef).findById(id)

    editOrgs(id, textBody, ach, loggedIn)
    Achievement.updateById(id)
      .withAttributes(
        'achievement_name -> textBody.get("achievement_name").head.head,
        'date -> textBody.get("date").head.head)

    Cert.updateById(ach.get.cert.get.id)
      .withAttributes(
        'exp_date -> textBody.get("exp_date").head.head)
    Redirect(routes.Show.achievement(id))
  }

  def editAmb(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val body: AnyContent = request.body
    val multipartBody = body.asMultipartFormData
    val textBody: Map[String, Seq[String]] = multipartBody.get.asFormUrlEncoded
    println(textBody)

    val ach = Achievement
      .joins(Achievement.orgRef)
      .joins(Achievement.ambRef).findById(id)

    editOrgs(id, textBody, ach, loggedIn)
    Achievement.updateById(id)
      .withAttributes(
        'achievement_name -> textBody.get("achievement_name").head.head,
        'date -> textBody.get("date").head.head)

    Ambassador.updateById(ach.get.amb.get.id)
      .withAttributes(
        'year -> textBody.get("year").head.head)

    Add.sendMail("Edit ach noti", routes.Show.achievement(id).absoluteURL())
    Redirect(routes.Show.achievement(id))
  }

  def del(id: Long) = StackAction(AuthorityKey -> Seq(Auth.Student)) { implicit request =>
    val ach = Achievement
      .joins(Achievement.accRef)
      .joins(Achievement.teacher_accRef)
      .joins(Achievement.orgRef)
      .joins(Achievement.compRef)
      .joins(Achievement.certRef).joins(Achievement.ambRef).findById(id)
    val canDel = ach.get.accs.map(_.username).contains(loggedIn.username.value) && ach.get.created_at.isAfter(LocalDate.now().minusDays(3))

    if (!canDel) {
      Ok(html.tarwised.Forb("Forbidden"))
    } else {
      editStudents(id, Map("student_ids" -> Seq("")), ach, loggedIn)
      editTeachers(id, Map("teacher_names" -> Seq("")), ach, loggedIn)
      editOrgs(id, Map("orgs" -> Seq("")), ach, loggedIn)
      ach.get.achievement_type match {
        case 1 => Competition.deleteById(ach.get.comp.get.id)
        case 2 => Cert.deleteById(ach.get.cert.get.id)
        case 3 => Ambassador.deleteById(ach.get.amb.get.id)
      }
      Achievement.deleteById(id)
      Redirect(routes.Main.home)
    }
  }

  protected val main: User => Template = html.main.apply
}


object Edit extends Edit
