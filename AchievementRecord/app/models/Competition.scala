package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

/**
  * Created by Pichai Sivawat on 4/10/2016.
  */
case class Competition(id: Long,
                       event_name: String,
                       topic: String,
                       level: String,
                       rank: String,
                       achievement_id: Long,
                       ach: Option[Achievement] = None)
object Competition extends SkinnyCRUDMapper[Competition] {
  override lazy val defaultAlias = createAlias("comp")
  override val tableName = "competitions"

  def create(event_name: String, topic: String, level: String, rank: String, achievement_id: Long) = {
    createWithNamedValues(
      column.event_name -> event_name,
      column.topic -> topic,
      column.level -> level,
      column.rank -> rank,
      column.achievement_id -> achievement_id
    )
  }

  override def extract(rs: WrappedResultSet, n: ResultName[Competition]): Competition = new Competition(
    id = rs.get(n.id),
    event_name = rs.get(n.event_name),
    topic = rs.get(n.topic),
    level = rs.get(n.level),
    rank = rs.get(n.rank),
    achievement_id = rs.get(n.achievement_id)
  )

  lazy val achRef = belongsToWithFk[Achievement](
    Achievement,
    "achievement_id",
    (comp, ach) => comp.copy(ach=ach))
}
