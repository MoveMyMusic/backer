package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Reads._
import play.api.libs.json._
import models.{JsonModels, Users, Teachers}
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
        val teachers = Teachers.byNameSubstring(name.toLowerCase).list.map(user => JsonModels.userJson(user._1, user._2, user._3))
        Ok(JsArray(teachers))
      }).getOrElse {
        Ok(JsArray(Teachers.all.list.map(user => JsonModels.userJson(user._1, user._2, user._3))))
      }
    }
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

            val (id, token) = Users.encryptInsert(name, Some(email), password)

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
