package models

import scalikejdbc._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 4/6/2016.
  */
case class Status(status_id: Int, status_name: String)
object Status extends SkinnyMapper[Status]{
  override val defaultAlias = createAlias("stat")
  override val tableName = "status"
  override val primaryKeyFieldName = "status_id"

  override def extract(rs: WrappedResultSet, n: ResultName[Status]): Status = autoConstruct(rs, n)
}
