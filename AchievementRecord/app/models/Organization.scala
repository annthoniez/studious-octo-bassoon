package models

import scalikejdbc._
import skinny.orm.SkinnyMapper

/**
  * Created by Pichai Sivawat on 4/10/2016.
  */
case class Organization(id: Long, organization_name: String, organization_type: String, achs: Seq[Achievement] = Nil)
object Organization extends SkinnyMapper[Organization] {
  override lazy val defaultAlias = createAlias("org")
  override val tableName = "organizations"

  override def extract(rs: WrappedResultSet, n: ResultName[Organization]): Organization = new Organization(
    id = rs.get(n.id),
    organization_name = rs.get(n.organization_name),
    organization_type = rs.get(n.organization_type)
  )

  lazy val achRef = hasManyThroughWithFk[Achievement](Organization_Achievement, Achievement, "organization_id", "achievement_id", (org, achs) => org.copy(achs = achs))
}
