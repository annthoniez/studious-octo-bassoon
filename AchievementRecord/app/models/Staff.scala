package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapperWithId

/**
  * Created by Pichai Sivawat on 4/6/2016.
  */
case class Staff(staff_id: Username, username: String, th_name: String, en_name: String, section_id: Int, mobile: String, email: String, acc: Option[Account] = None, section: Option[Section] = None)
object Staff extends SkinnyCRUDMapperWithId[Username, Staff]{
  override val defaultAlias = createAlias("staff")
  override val tableName = "staff"
  override val primaryKeyFieldName = "staff_id"
  override def idToRawValue(id: Username): Any = id.value
  override def rawValueToId(value: Any): Username = Username(value.toString)

  override def extract(rs: WrappedResultSet, n: ResultName[Staff]): Staff = new Staff(
    staff_id = rs.get(n.staff_id),
    username = rs.get(n.username),
    th_name = rs.get(n.th_name),
    en_name = rs.get(n.en_name),
    section_id = rs.get(n.section_id),
    mobile = rs.get(n.mobile),
    email = rs.get(n.email)
  )

  lazy val accRef = hasOneWithFk[Account](Account, "username", (staff, acc) => staff.copy(acc = acc))
  lazy val sectionRef = belongsToWithFk[Section](Section, "section_id", (staff, section) => staff.copy(section = section))

}
