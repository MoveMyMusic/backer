package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import org.codehaus.jackson.node.ObjectNode
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object Teachers extends Controller {

  def get(id: Long) = Action {
    val teacher = Json.obj(
      "id" -> id,
      "name" -> "Travis",
      "email" -> "travis@gamil.com",
      "token" -> "123345"
    )
    Ok(teacher)
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
  def put(id: Long) = Action {
     val teacher = Json.obj(
       "id" -> id,
       "name" -> "Travis",
       "email" -> "travis@gamil.com",
       "token" -> "123345"
     )

    Ok(teacher)
  }

  def post = Action {
    val teacher = Json.obj(
      "id" -> 2,
      "name" -> "Travis",
      "email" -> "travis@gamil.com",
      "token" -> "123345"
    )

    Ok(teacher)

  }

}
