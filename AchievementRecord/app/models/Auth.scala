package models

import scalikejdbc.TypeBinder

/**
  * Created by Pichai Sivawat on 3/28/2016.
  */

sealed trait Auth

object Auth {
  case object Staff extends Auth
  case object Student extends Auth
  case object Teacher extends Auth

  def valueOf(value: Int): Auth = value match {
    case 1 => Teacher
    case 2 => Staff
    case 3 => Student
    case _ => throw new IllegalArgumentException()
  }

  implicit val typeBinder: TypeBinder[Auth] = TypeBinder.int.map(valueOf)
}
