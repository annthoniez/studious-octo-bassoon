package models

import java.time.LocalDate

import scalikejdbc._
import scalikejdbc.jsr310._
import skinny.orm.SkinnyCRUDMapper
import skinny.orm.feature.TimestampsFeature

/**
  * Created by Pichai Sivawat on 3/29/2016.
  */

case class Achievement(id: Long,
                       achievement_name: String,
                       date: LocalDate,
                       photo: String,
                       reward: String,
                       category: String,
                       achievement_type: Int,
                       created_at: LocalDate,
                       updated_at: LocalDate,
                       t_accs: Seq[Teacher] = Nil,
                       accs: Seq[Student] = Nil,
                       orgs: Seq[Organization] = Nil,
                       comp: Option[Competition] = None,
                       cert: Option[Cert] = None,
                       amb: Option[Ambassador] = None)

object Achievement extends SkinnyCRUDMapper[Achievement] with TimestampsFeature[Achievement] {
  override lazy val defaultAlias = createAlias("ach")
  override val tableName = "achievements"

  override def extract(rs: WrappedResultSet, n: ResultName[Achievement]):Achievement = new Achievement(
    id = rs.get(n.id),
    achievement_name = rs.get(n.achievement_name),
    date = rs.get(n.date),
    photo = rs.get(n.photo),
    reward = rs.get(n.reward),
    category = rs.get(n.category),
    achievement_type = rs.get(n.achievement_type),
    created_at = rs.get(n.created_at),
    updated_at = rs.get(n.updated_at)
  )

  lazy val teacher_accRef = hasManyThroughWithFk[Teacher](
    Teacher_Achievement,
    Teacher,
    "achievement_id",
    "teacher_id",
    (t_ach, t_accs) => t_ach.copy(t_accs = t_accs))

  lazy val accRef = hasManyThroughWithFk[Student](
    Student_Achievement,
    Student,
    "achievement_id",
    "student_id",
    (ach, accs) => ach.copy(accs = accs))

  lazy val orgRef = hasManyThroughWithFk[Organization](
    Organization_Achievement,
    Organization,
    "achievement_id",
    "organization_id",
    (ach, orgs) => ach.copy(orgs = orgs))

  lazy val compRef = hasOneWithFk[Competition](
    Competition,
    "achievement_id",
    (ach, comp) => ach.copy(comp=comp))

  lazy val certRef = hasOneWithFk[Cert](
    Cert,
    "achievement_id",
    (ach, cert) => ach.copy(cert=cert))

  lazy val ambRef = hasOneWithFk[Ambassador](
    Ambassador,
    "achievement_id",
    (ach, amb) => ach.copy(amb=amb))

  def create(achievement_name: String, date: String, photo: String, reward: String, category: String, achievement_type: Int): Long = {
    createWithNamedValues(
      column.achievement_name -> achievement_name,
      column.date -> date,
      column.photo -> photo,
      column.reward -> reward,
      column.category -> category,
      column.achievement_type -> achievement_type
    )
  }

  def getAchWithChild(id: Long): Option[Achievement] =
    Achievement
      .joins(Achievement.compRef)
      .joins(Achievement.certRef)
      .joins(Achievement.ambRef).findById(id)

  def getStudentInAch(achs: Seq[Achievement]): Seq[Seq[Student]] =
    Achievement
      .joins(Achievement.accRef)
      .findAll()
      .filter(a =>
        achs.map(_.id).contains(a.id))
      .map(_.accs)

  def getTeacherInAch(achs: Seq[Achievement]): Seq[Seq[Teacher]] =
    Achievement
      .joins(Achievement.teacher_accRef)
      .findAll()
      .filter(a =>
        achs.map(_.id).contains(a.id))
      .map(_.t_accs)

  def getOrgInAch(achs: Seq[Achievement]): Seq[Seq[Organization]] =
    Achievement
      .joins(Achievement.orgRef)
      .findAll()
      .filter(a =>
        achs.map(_.id).contains(a.id))
      .map(_.orgs)

//  private val a = syntax("a")
//  private val auto = autoSession
//  override val tableName = "achievement"
//
//  def apply(a: SyntaxProvider[Achievement])(rs: WrappedResultSet): Achievement = autoConstruct(rs, a)
//
//  def findById(id: Int)(implicit s: DBSession = auto): Option[Achievement] = withSQL {
//    select.from(Achievement as a).where.eq(a.achievement_id, id)
//  }.map(Achievement(a)).single.apply()
//
//  def findAll()(implicit s: DBSession = auto): Seq[Achievement] = withSQL {
//    select.from(Achievement as a)
//  }   .map(Achievement(a)).list.apply()
}
