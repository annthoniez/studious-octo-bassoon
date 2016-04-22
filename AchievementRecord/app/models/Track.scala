package models

import scalikejdbc._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 4/5/2016.
  */
case class Track(track_id: Int,
                 th_name: String,
                 en_name: String)
object Track extends SkinnyMapper[Track]{
  override val defaultAlias = createAlias("track")
  override val tableName = "track"
  override val primaryKeyFieldName = "track_id"

  override def extract(rs: WrappedResultSet, n: ResultName[Track]): Track = autoConstruct(rs, n)
}
