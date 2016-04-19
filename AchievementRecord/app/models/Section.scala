package models

import scalikejdbc._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 4/6/2016.
  */
case class Section(section_id: Int,
                   th_name: String,
                   en_name: String)
object Section extends SkinnyMapper[Section] {
  override val defaultAlias = createAlias("section")
  override val tableName = "section"
  override val primaryKeyFieldName = "section_id"

  override def extract(rs: WrappedResultSet, n: ResultName[Section]): Section = autoConstruct(rs, n)
}
