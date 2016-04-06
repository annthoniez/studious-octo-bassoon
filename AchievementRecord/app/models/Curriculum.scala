package models

import scalikejdbc._
import skinny.orm.{Alias, SkinnyMapper}

/**
  * Created by Pichai Sivawat on 4/5/2016.
  */
case class Curriculum(curriculum_id: Int, th_name: String, en_name: String)
object Curriculum extends SkinnyMapper[Curriculum]{
  override val defaultAlias = createAlias("cur")
  override val tableName = "curriculum"
  override val primaryKeyFieldName = "curriculum_id"

  override def extract(rs: WrappedResultSet, n: ResultName[Curriculum]): Curriculum = autoConstruct(rs, n)
}
