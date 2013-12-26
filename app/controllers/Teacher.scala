package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Reads._
import play.api.libs.json._
import models.{StudentsTeachers, JsonModels, Users, Teachers}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session
import Database.threadLocalSession
import play.api.Play.current
import play.api.db.DB
import scalaz.{NonEmptyList, Validation}
import scalaz.syntax.validation._


// you need this import to have combinators
import play.api.libs.functional.syntax._
/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object Teacher extends Controller {

  val log = Logger

  def get(id: Int) = Action {
    Database.forDataSource(DB.getDataSource()) withSession {

      val u = Teachers.byId(id)

      val teacher = Json.obj(
          "id" -> u._1,
          "name" -> u._2,
          "email" -> u._3
        )
        Ok(teacher)
      }
  }


  def getAll = Action { request =>

    Database.forDataSource(DB.getDataSource()) withSession {
        request.getQueryString("name").map(name => {
        val teachers = Teachers.byNameSubstring("%" + name.toLowerCase + "%").list.map(user => JsonModels.userJson(user._1, user._2, user._3))
        Ok(JsArray(teachers))
      }).getOrElse {
        Ok(JsArray(Teachers.all.list.map(user => JsonModels.userJson(user._1, user._2, user._3))))
      }
    }
  }

  /** Updates a teacher */
  def put(id: Int) = {
    val input = (
      (__ \ 'name).readOpt[String] and
        (__ \ 'password).readOpt[String] and
        (__ \ 'email).readOpt[String]
      ) tupled

    val update: (Option[String], Option[String], Option[String]) => (Int, Session) => (Int, String, Option[String], String) =
      (name, password, email) => (id, session) => {
        val q = for {
//          t <- Teachers if t.id == id
          u <- Users if u.id == id
        } yield (u.name, u.password, u.email)
        name.foreach(q.update(_))
        password.foreach(q.update(_))
        email.foreach(q.update(_))
      }

  }

  def delete(id: Int) = Action {
    Database.forDataSource(DB.getDataSource()) withSession {

      val delStQuery = for {
        st <- StudentsTeachers if (st.teacherId is id)
      } yield st

      delStQuery.delete

      val delQuery = for {
        teacher <- Teachers if (teacher.id is id)
      } yield teacher
      val num = delQuery.delete

      if (num > 0) {
        val delUserQuery = for {
          u <- Users if (u.id is id)
        } yield u

        delUserQuery.delete
      }
      Ok("Deleted  Teacher")

    }
  }



  def post = {

    val input = (
      (__ \ 'name).read[String] and
      (__ \ 'password).read[String] and
      (__ \ 'email).read[String]
    ) tupled

    val insert : (String, String, String) => Session => (Int, String, Option[String], String) = (name, password, email) => (session) => {
      val (id, token) = Users.encryptInsert(name, Some(email), password)
      Teachers.insert(id)
      (id, name, Some(email), token)
    }

    Database.forDataSource(DB.getDataSource()).withSession(s => {
      JsonInsert(input)(insert.tupled)(JsonModels.userWithToken.tupled)(s).action
    })
  }

}
