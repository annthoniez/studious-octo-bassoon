package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapperWithId

/**
  * Created by Pichai Sivawat on 4/5/2016.
  */
case class Student(student_id: Username,
                   username: String,
                   th_name: String,
                   en_name: String,
                   curriculum_id: Int,
                   track_id: Int,
                   email: String,
                   achs: Seq[Achievement] = Nil,
                   acc: Option[Account] = None,
                   curri: Option[Curriculum] = None,
                   track: Option[Track] = None)

object Student extends SkinnyCRUDMapperWithId[Username, Student]{
  override val defaultAlias = createAlias("stu")
  override val tableName = "student"
  override val primaryKeyFieldName = "student_id"
  override def idToRawValue(id: Username): Any = id.value
  override def rawValueToId(value: Any): Username = Username(value.toString)

  override def extract(rs: WrappedResultSet, n: ResultName[Student]): Student = new Student(
    student_id = rs.get(n.student_id),
    username = rs.get(n.username),
    th_name = rs.get(n.th_name),
    en_name = rs.get(n.en_name),
    curriculum_id = rs.get(n.curriculum_id),
    track_id = rs.get(n.track_id),
    email = rs.get(n.email)
  )

  lazy val achRef = hasManyThroughWithFk[Achievement](
    Student_Achievement,
    Achievement,
    "student_id",
    "achievement_id",
    (acc, achs) => acc.copy(achs = achs))

  lazy val accRef = hasOneWithFk[Account](
    Account,
    "username",
    (stu, acc) => stu.copy(acc = acc))

  lazy val curriRef = belongsToWithFk[Curriculum](
    Curriculum,
    "curriculum_id",
    (stu, curri) => stu.copy(curri = curri)).byDefault

  lazy val trackRef = belongsToWithFk[Track](
    Track,
    "track_id",
    (stu, track) => stu.copy(track = track)).byDefault

  def getProfile(username: String): Student =
    models.Student
      .joins(models.Student.curriRef)
      .joins(models.Student.trackRef).findAll()
      .filter(_.student_id.value == username).head

  def getAchs(username: String): Seq[Achievement] =
    models.Student
      .joins(models.Student.achRef).findAll()
      .filter(_.username == username).head.achs


}
