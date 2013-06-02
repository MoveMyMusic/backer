package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import org.codehaus.jackson.node.ObjectNode
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._
import models.{Users, Teachers}
import java.util.Date
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession
import play.api.Play.current
import play.api.db.DB
import org.apache.commons.codec.digest.DigestUtils


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

      val userById = for {
        id <- Parameters[Int]
        u <- Users if u.id is id
      } yield (u.id, u.name, u.email)

      val u = userById(id).first

      val teacher = Json.obj(
          "id" -> u._1,
          "name" -> u._2,
          "email" -> u._3
        )
        Ok(teacher)
      }
  }


  def getAll = Action {

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
      log.info("json body:" + json)
      json.validate[(String, String, String)](input).map {
        case (name, password, email) => {
          val salt = new String(DigestUtils.sha1Hex(new Date toString))
          val sha1pass = new String(DigestUtils.sha1Hex(password + salt))
          val token = new String(DigestUtils.sha1Hex(new Date toString))

          Database.forDataSource(DB.getDataSource()) withSession {

            val id = Users.forInsert.insert(name, email, sha1pass, salt, token)
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
