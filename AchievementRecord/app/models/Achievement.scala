package models

import java.time.LocalDate

import scalikejdbc._
import scalikejdbc.jsr310._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 3/29/2016.
  */

case class Achievement(id: Long, achievement_name: String, date: LocalDate, photo: String, accs: Seq[Student] = Nil)

object Achievement extends SkinnyMapper[Achievement] {
  override lazy val defaultAlias = createAlias("ach")
  override val tableName = "achievements"

  override def extract(rs: WrappedResultSet, n: ResultName[Achievement]):Achievement = new Achievement(
    id = rs.get(n.id),
    achievement_name = rs.get(n.achievement_name),
    date = rs.get(n.date),
    photo = rs.get(n.photo)
  )

  lazy val accRef = hasManyThroughWithFk[Student](
    Student_Achievement, Student, "achievement_id", "student_id", (ach, accs) => ach.copy(accs = accs)
  )
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
