package models

import java.time.LocalDate

import scalikejdbc._
import scalikejdbc.jsr310._
import skinny.orm.SkinnyMapper
/**
  * Created by Pichai Sivawat on 4/10/2016.
  */
case class Cert(id: Long, exp_date: LocalDate, achievement_id: Long, ach: Option[Achievement] = None)
object Cert extends SkinnyMapper[Cert] {
  override lazy val defaultAlias = createAlias("cert")
  override val tableName = "certs"

  override def extract(rs: WrappedResultSet, n: ResultName[Cert]): Cert = new Cert(
    id = rs.get(n.id),
    exp_date = rs.get(n.exp_date),
    achievement_id = rs.get(n.achievement_id)
  )

  lazy val achRef = belongsToWithFk[Achievement](Achievement, "achievement_id", (cert, ach) => cert.copy(ach=ach))
}
