package models

import scala.slick.driver.PostgresDriver.simple._
import org.apache.commons.codec.digest.DigestUtils
import java.util.Date

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object Users extends Table[(Int, String, Option[String], String, String, String)]("users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[Option[String]]("email")
  def password = column[String]("password")
  def salt = column[String]("salt")
  def token = column[String]("token")
  def forInsert = name ~ password ~ email ~ salt ~ token returning id
  def *  = id ~ name ~ email ~ password ~ salt ~ token

  /** Insert generating salted password and returning Id and Token */
  def encryptInsert(n: String, e: Option[String], p: String)(implicit ses: Session): (Int, String) = {
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

  def byNameSubstring(name: String) = for {
//    name <- Parameters[String]
    u <- Users if u.name.toLowerCase like ("%" + name + "%")
    t <- Teachers if t.id is u.id
  } yield (u.id, u.name, u.email)

  val all = for {
    u <- Users
    t <- Teachers if t.id is u.id
  } yield (u.id, u.name, u.email)

  def byId(id: Int)(implicit sess: Session) = {
    val userById = for {
      id <- Parameters[Int]
      u <- Users if u.id is id
      t <- Teachers if t.id is u.id
    } yield (u.id, u.name, u.email)

    userById(id).first
  }



}

object Students extends Table[(Int)]("students") {
  def id = column[Int]("id", O.PrimaryKey)
  def user = foreignKey("user_fk", id, Users)(_.id)
  def * = id

  def byId(id: Int)(implicit sess: Session) = {
    val userById = for {
      i <- Parameters[Int]
      u <- Users if u.id is i
      s <- Students if s.id is u.id
    } yield (u.id, u.name, u.email)

    userById(id).first
  }
}

object StudentsTeachers extends Table[(Int, Int)]("students_teachers") {
  def studentId = column[Int]("student_id")
  def teacherId = column[Int]("teacher_id")
  def * = studentId ~ teacherId

  val forInsert = for {
    (sid, tid) <- Parameters[(Int, Int)]
    student <- Students if student.id is sid
    teacher <- Teachers if teacher.id is tid
  } yield {
    (student.id, teacher.id)
  }

}

object SaveData extends Table[(Option[Int], Int, String)]("save_data") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def user_id = column[Int]("user_id")
  def user = foreignKey("user_fk", user_id, Users)(_.id)
  def saveData = column[String]("save_data")

  def * = id.? ~ user_id ~ saveData
}