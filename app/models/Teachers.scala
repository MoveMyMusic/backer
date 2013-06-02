package models

import scala.slick.driver.PostgresDriver.simple._

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
