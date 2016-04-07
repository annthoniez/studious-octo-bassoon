package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapperWithId

/**
  * Created by Pichai Sivawat on 4/6/2016.
  */
case class Teacher(teacher_id: Username, username: String, th_prename: String, th_name: String, en_prename: String, en_name: String, status_id: Int, mobile: String, email: String, achs: Seq[Achievement] = Nil, acc: Option[Account] = None, stat: Option[Status] = None)
object Teacher extends SkinnyCRUDMapperWithId[Username, Teacher]{
  override val defaultAlias = createAlias("teacher")
  override val tableName = "teacher"
  override val primaryKeyFieldName = "teacher_id"
  override def idToRawValue(id: Username): Any = id.value
  override def rawValueToId(value: Any): Username = Username(value.toString)

  override def extract(rs: WrappedResultSet, n: ResultName[Teacher]): Teacher = new Teacher(
    teacher_id = rs.get(n.teacher_id),
    username = rs.get(n.username),
    th_prename = rs.get(n.th_prename),
    th_name = rs.get(n.th_name),
    en_prename = rs.get(n.en_prename),
    en_name = rs.get(n.en_name),
    status_id = rs.get(n.status_id),
    mobile = rs.get(n.mobile),
    email = rs.get(n.email)
  )
  lazy val achRef = hasManyThroughWithFk[Achievement](Teacher_Achievement, Achievement, "teacher_id", "achievement_id", (teacher, achs) => teacher.copy(achs = achs))
  lazy val accRef = hasOneWithFk[Account](Account, "username", (teacher, acc) => teacher.copy(acc = acc))
  lazy val statRef = belongsToWithFk[Status](Status, "status_id", (teacher, stat) => teacher.copy(stat = stat))
}
