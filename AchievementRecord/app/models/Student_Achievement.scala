package models

import skinny.orm.SkinnyJoinTable

/**
  * Created by Pichai Sivawat on 3/29/2016.
  */
case class Student_Achievement(student_id: Username, achievement_id: Long)

object Student_Achievement extends SkinnyJoinTable[Student_Achievement] {
  override lazy val defaultAlias = createAlias("stu_ach")
  override val tableName = "student_achievement"
}
