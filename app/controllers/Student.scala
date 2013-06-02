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

    Database.forDataSource(DB.getDataSource()) withSession {

        val students = request.getQueryString("name").map(name => {
        val studentQ = for {
          user <- Users if user.name.toLowerCase like "%" + name.toLowerCase + "%"
          student <- Students if user.id is student.id
        } yield (user.id, user.name, user.email)

        studentQ.list
      }).orElse(request.getQueryString("teacherId").map(Integer.parseInt(_)).map(teacherId => {
        val studentQ = for {
          tid <- Parameters[Int]
          user <- Users
          student <- Students if (student.id is user.id)
          st <- StudentsTeachers if ((st.teacherId is tid) && (st.studentId is student.id))
        } yield (user.id, user.name, user.email)

        studentQ(teacherId).list

      })).getOrElse {
        Students.all.list
      }.map(u => JsonModels.userJson(u._1, u._2, u._3))

      Ok(JsArray(students))
    }
  }

  /** Updates a teacher */
  def put(id: Long) = Action {
     val teacher = Json.obj(
       "id" -> 2,
       "name" -> "NOT IMPLEMENTED",
       "email" -> "travis@gamil.com",
       "token" -> "123345"
     )

    Ok(teacher)
  }

  def post = Action { request =>

    val input = (
      (__ \ 'name).read[String] and
        (__ \ 'password).readNullable[String] and
        (__ \ 'email).readNullable[String]
      ) tupled



    request.body.asJson.map( { json =>
      json.validate[(String, Option[String], Option[String])](input).map {
        case (name, password, email) => {

          Database.forDataSource(DB.getDataSource()) withSession {

            val (id, token) = Users.encryptInsert(name, email, password.getOrElse(""))

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

  val teacherIdInput = (
    (__ \ 'teacherId).readNullable[Int]
    )

  def putTeacher(studentId: Int) = Action { request =>


    request.body.asJson.map( { json =>
      json.validate[(Option[Int])](teacherIdInput).map {
        case (Some(teacherId)) => {

          Database.forDataSource(DB.getDataSource()) withSession {
            val res = StudentsTeachers.forInsert.first(studentId, teacherId)
            StudentsTeachers.insert(res._1, res._2)
          }
        }
      }
    })

    Ok("Added teacher to student.")
  }

  def delTeacher(studentId: Int, teacherId: Int) = Action { request =>

    Database.forDataSource(DB.getDataSource()) withSession {
      val delQuery = for {
        st <- StudentsTeachers if ( (st.studentId is studentId) && (st.teacherId is teacherId))
      } yield st

      delQuery.delete
      Ok("Removed teacher from student.")

    }
  }

}
