package controllers

import play.api.mvc._
import scala.slick.driver.PostgresDriver.simple._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.DB
import models._
import Database.threadLocalSession
import play.api.Play.current
import scala.Some


/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object Student extends Controller {

  def get(id: Int) = Action {
    Database.forDataSource(DB.getDataSource()) withSession {

      val u = Students.byId(id)

      val student = Json.obj(
        "id" -> u._1,
        "name" -> u._2,
        "email" -> u._3
      )
      Ok(student)
    }

  }

  def getAll = Action { request =>

    val students = Json.obj(
      "students" -> Json.arr(
        Json.obj(
          "id" -> 1,
          "name" -> "Johnny",
          "token" -> "12345"
        ),
        Json.obj(
          "id" -> 2,
          "name" -> "Suzie",
          "token" -> "12345"
        )
      )
    )

    Ok(students)
  }

  /** Updates a teacher */
  def put(id: Long) = Action {
     val teacher = Json.obj(
       "id" -> 2,
       "name" -> "Travis",
       "email" -> "travis@gamil.com",
       "token" -> "123345"
     )

    Ok(teacher)
  }

  def post = Action { request =>

    val input = (
      (__ \ 'name).read[String] and
        (__ \ 'password).readNullable[String] and
        (__ \ 'email).read[String]
      ) tupled



    request.body.asJson.map( { json =>
      json.validate[(String, Option[String], String)](input).map {
        case (name, password, email) => {

          Database.forDataSource(DB.getDataSource()) withSession {

            val (id, token) = Users.encryptInsert(name, password, email)

            Students.insert(id)

            Ok(Json.obj(
              "id" -> id,
              "name" -> name,
              "email" -> email,
              "token" -> token
            ))
          }

        }
      }.recoverTotal{
        e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
      }
    }).getOrElse {
      BadRequest("Expecting Json data")
    }

  }

  def getTeachers(studentId: Int) = Action { request =>

    val teacherQuery = for {
      sid <- Parameters[Int]
      student <- Students if (student.id is sid)
      st <- StudentsTeachers if (st.studentId is student.id)
      teacher <- Teachers if (teacher.id is st.teacherId)
      user <- Users if (user.id is teacher.id)
    } yield (user.id, user.name, user.email)

    Database.forDataSource(DB.getDataSource()) withSession {
      val teachers = teacherQuery.list(studentId).map(t => JsonModels.userJson(t._1, t._2, t._3))
      Ok(JsArray(teachers))
    }

  }

  def putTeachers(studentId: Int) = Action { request =>
    val input = (
      (__ \ 'teacherId).readNullable[Int]
      )

    request.body.asJson.map( { json =>
      json.validate[(Option[Int])](input).map {
        case (Some(teacherId)) => {

          Database.forDataSource(DB.getDataSource()) withSession {
            val res = StudentsTeachers.forInsert.first(studentId, teacherId)
            StudentsTeachers.insert(res._1, res._2)
          }
        }
      }
    })

    Ok("Added teacher.")
  }

  def delTeachers(studentId: Long, teacherId: Long) = Action {
    val teachers = Json.obj(
      "teachers" -> Json.arr(1)
    )
    Ok(teachers)
  }

}
