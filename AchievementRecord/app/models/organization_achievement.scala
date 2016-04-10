package models

import skinny.orm.SkinnyJoinTable

/**
  * Created by Pichai Sivawat on 4/10/2016.
  */
case class Organization_Achievement(organization_id: Long, achievement_id: Long)
object Organization_Achievement extends SkinnyJoinTable[Organization_Achievement] {
  override lazy val defaultAlias = createAlias("org_ach")
  override val tableName = "organization_achievement"
}
