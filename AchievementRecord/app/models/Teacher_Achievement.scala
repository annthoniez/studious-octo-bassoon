package models

import skinny.orm.SkinnyJoinTable

/**
  * Created by Pichai Sivawat on 4/7/2016.
  */
case class Teacher_Achievement(teacher_id: Username,
                               achievement_id: Long)

object Teacher_Achievement extends SkinnyJoinTable[Teacher_Achievement] {
  override lazy val defaultAlias = createAlias("teacher_ach")
  override val tableName = "teacher_achievement"

  def create(teacher_id: String, achievement_id: Long) = {
    createWithNamedValues(
      column.teacher_id -> teacher_id,
      column.achievement_id -> achievement_id
    )
  }
}
