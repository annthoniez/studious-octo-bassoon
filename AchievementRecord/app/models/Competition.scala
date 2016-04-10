package models

import scalikejdbc._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 4/10/2016.
  */
case class Competition(id: Long, event_name: String, topic: String, level: String, rank: String, achievement_id: Long)
object Competition extends SkinnyMapper[Competition] {
  override lazy val defaultAlias = createAlias("comp")
  override val tableName = "competitions"

  override def extract(rs: WrappedResultSet, n: ResultName[Competition]): Competition = new Competition(
    id = rs.get(n.id),
    event_name = rs.get(n.event_name),
    topic = rs.get(n.topic),
    level = rs.get(n.level),
    rank = rs.get(n.rank),
    achievement_id = rs.get(n.achievement_id)
  )
}
