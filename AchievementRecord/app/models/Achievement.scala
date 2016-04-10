package models

import java.time.LocalDate

import scalikejdbc._
import scalikejdbc.jsr310._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 3/29/2016.
  */

case class Achievement(id: Long, achievement_name: String, date: LocalDate, photo: String, reward: String, category: String, achievement_type: Int, t_accs: Seq[Teacher] = Nil, accs: Seq[Student] = Nil, orgs: Seq[Organization] = Nil)

object Achievement extends SkinnyMapper[Achievement] {
  override lazy val defaultAlias = createAlias("ach")
  override val tableName = "achievements"

  override def extract(rs: WrappedResultSet, n: ResultName[Achievement]):Achievement = new Achievement(
    id = rs.get(n.id),
    achievement_name = rs.get(n.achievement_name),
    date = rs.get(n.date),
    photo = rs.get(n.photo),
    reward = rs.get(n.reward),
    category = rs.get(n.category),
    achievement_type = rs.get(n.achievement_type)
  )

  lazy val teacher_accRef = hasManyThroughWithFk[Teacher](Teacher_Achievement, Teacher, "achievement_id", "teacher_id", (t_ach, t_accs) => t_ach.copy(t_accs = t_accs))
  lazy val accRef = hasManyThroughWithFk[Student](
    Student_Achievement, Student, "achievement_id", "student_id", (ach, accs) => ach.copy(accs = accs)
  )
  lazy val orgRef = hasManyThroughWithFk[Organization](Organization_Achievement, Organization, "achievement_id", "organization_id", (ach, orgs) => ach.copy(orgs = orgs))

  def getStudentInAch(achs: Seq[Achievement]): Seq[Seq[Student]] = Achievement.joins(Achievement.accRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.accs)

  def getTeacherInAch(achs: Seq[Achievement]): Seq[Seq[Teacher]] = Achievement.joins(Achievement.teacher_accRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.t_accs)

  def getOrgInAch(achs: Seq[Achievement]): Seq[Seq[Organization]] = Achievement.joins(Achievement.orgRef).findAll().filter(a => achs.map(_.id).contains(a.id)).map(_.orgs)
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
