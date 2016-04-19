package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

/**
  * Created by Pichai Sivawat on 4/10/2016.
  */
case class Ambassador(id: Long,
                      year: String,
                      achievement_id: Long,
                      ach: Option[Achievement] = None)
object Ambassador extends SkinnyCRUDMapper[Ambassador] {
  override lazy val defaultAlias = createAlias("amb")
  override val tableName = "ambassadors"

  def create(year: String, achievement_id: Long) = {
    createWithNamedValues(
      column.year -> year,
      column.achievement_id -> achievement_id
    )
  }

  override def extract(rs: WrappedResultSet, n: ResultName[Ambassador]): Ambassador = new Ambassador(
    id = rs.get(n.id),
    year = rs.get(n.year),
    achievement_id = rs.get(n.achievement_id)
  )

  lazy val achRef = belongsToWithFk[Achievement](
    Achievement,
    "achievement_id",
    (amb, ach) => amb.copy(ach=ach))
}
