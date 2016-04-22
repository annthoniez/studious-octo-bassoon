package models

import scalikejdbc._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 4/4/2016.
  */
case class Role(role_id: Int,
                role_name: String,
                accounts: Seq[Account] = Nil)

object Role extends SkinnyMapper[Role] {
  override lazy val defaultAlias = createAlias("role")
  override val tableName = "role"
  override val primaryKeyFieldName = "role_id"

  override def extract(rs: WrappedResultSet, n: ResultName[Role]): Role = new Role(
    role_id = rs.get(n.role_id),
    role_name = rs.get(n.role_name)
  )
}
