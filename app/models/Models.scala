package models

import scala.slick.driver.PostgresDriver.simple._
import java.lang.String
import scala.Predef.String
import org.apache.commons.codec.digest.DigestUtils
import java.util.Date
import play.api.libs.json.Json

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object Users extends Table[(Int, String, String, String, String, String)]("users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def password = column[String]("password")
  def salt = column[String]("salt")
  def token = column[String]("token")
  def forInsert = name ~ password ~ email ~ salt ~ token returning id
  def *  = id ~ name ~ email ~ password ~ salt ~ token

  /** Insert generating salted password and returning Id and Token */
  def encryptInsert(n: String, e: String, p: String)(implicit ses: Session): (Int, String) = {
    val salt = new String(DigestUtils.sha1Hex(new Date toString))
    val sha1pass = new String(DigestUtils.sha1Hex(p + salt))
    val token = new String(DigestUtils.sha1Hex(new Date toString))
    (forInsert.insert(n, sha1pass, e, salt, token), token)

  }

  /** find id, name, email by id */
  def byId(id: Int)(implicit sess: Session) = {
    val userById = for {
      id <- Parameters[Int]
      u <- Users if u.id is id
    } yield (u.id, u.name, u.email)

    userById(id).first
  }

}

object Teachers extends Table[(Int)]("teachers") {
  def id = column[Int]("id", O.PrimaryKey)
  def user = foreignKey("user_fk", id, Users)(_.id)
  def * = id
}

object Students extends Table[(Int)]("students") {
  def id = column[Int]("id", O.PrimaryKey)
  def user = foreignKey("user_fk", id, Users)(_.id)
  def * = id
}

object SaveData extends Table[(Option[Int], Int, String)]("save_data") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def user_id = column[Int]("user_id")
  def user = foreignKey("user_fk", user_id, Users)(_.id)
  def saveData = column[String]("save_data")

  def * = id.? ~ user_id ~ saveData
}