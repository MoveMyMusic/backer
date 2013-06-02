package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Reads._
import play.api.libs.json._
import models.{Users, Teachers}
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession
import play.api.Play.current
import play.api.db.DB


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

      val u = Users.byId(id)

      val teacher = Json.obj(
          "id" -> u._1,
          "name" -> u._2,
          "email" -> u._3
        )
        Ok(teacher)
      }
  }


  def getAll = Action { request =>

//    val userByToken = for {
//      token <- Parameters[String]
//      u <- Users if u.token == token
//    }
//
//    request.headers.get("Authorization").map()

    val teachers = Json.obj(
      "teachers" -> Json.arr(
        Json.obj(
          "id" -> 1,
          "name" -> "Travis",
          "email" -> "travis@gmail.com",
          "token" -> "12345"
        ),
        Json.obj(
          "id" -> 2,
          "name" -> "kiki",
          "email" -> "travis@gmail.com",
          "token" -> "12345"
        )
      )
    )

    Ok(teachers)
  }

  /** Updates a teacher */
  def put(id: Int) = Action {
     val teacher = Json.obj(
       "id" -> id,
       "name" -> "Travis",
       "email" -> "travis@gamil.com",
       "token" -> "123345"
     )

    Ok(teacher)
  }

  def post = Action { request =>


    val input = (
      (__ \ 'name).read[String] and
      (__ \ 'password).read[String] and
      (__ \ 'email).read[String]
    ) tupled



    request.body.asJson.map( { json =>
      json.validate[(String, String, String)](input).map {
        case (name, password, email) => {

          Database.forDataSource(DB.getDataSource()) withSession {

            val (id, token) = Users.encryptInsert(name, password, email)

            Teachers.insert(id)

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

}
