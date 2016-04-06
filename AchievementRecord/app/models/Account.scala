package models

import scalikejdbc._
import skinny.orm.{SkinnyCRUDMapperWithId, SkinnyMapper, feature}

/**
  * Created by Pichai Sivawat on 3/28/2016.
  */
case class Username(value: String)
object Username {
  implicit val typeBinder: TypeBinder[Username] = TypeBinder.string.map(Username.apply)
}

case class Account(username: Username, password: String, role_id: Int, achs: Seq[Achievement] = Nil, stu: Option[Student] = None)

object Account extends SkinnyCRUDMapperWithId[Username, Account] {
  override lazy val defaultAlias = createAlias("usr")
  override val tableName = "user"
  override def primaryKeyFieldName = "username"
  override def idToRawValue(id: Username) = id.value
  override def rawValueToId(value: Any) = Username(value.toString)

  override def extract(rs: WrappedResultSet, n: ResultName[Account]):Account = new Account(
    username = rs.get(n.username),
    password = rs.get(n.password),
    role_id = rs.get(n.role_id)
  )

  lazy val achRef = hasManyThrough[Achievement](Student_Achievement, Achievement, (acc, achs) => acc.copy(achs = achs))
  lazy val stuRef = belongsToWithFk[Student](Student, "username", (acc, stu) => acc.copy(stu = stu)).byDefault

  def authenticate(username: String, password: String): Option[Account] = findByUsername(username).filter({ account => password == account.password})

  def findByUsername(username: String): Option[Account] = Account.findById(Username(username))

  //  private val a = syntax("a")
//  private val auto = AutoSession
//  override val tableName = "user"
//
//  def apply(a: SyntaxProvider[Account])(rs: WrappedResultSet): Account = autoConstruct(rs, a)
//
//  def authenticate(email: String, password: String)(implicit s: DBSession = auto): Option[Account] = {
//    findByEmail(email).filter({account => password == account.password})
//  }
//
//  def findByEmail(email: String)(implicit s: DBSession = auto): Option[Account] = withSQL {
//    select.from(Account as a).where.eq(a.email, email)
//  }.map(Account(a)).single.apply()
//
//  def findById(id: Int)(implicit s: DBSession = auto): Option[Account] = withSQL {
//    select.from(Account as a).where.eq(a.id, id)
//  }.map(Account(a)).single.apply()
//
//  def findAll()(implicit s: DBSession = auto): Seq[Account] = withSQL {
//    select.from(Account as a)
//  }.map(Account(a)).list.apply()

}
