package models

import play.api.libs.json.{JsObject, Json}

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object JsonModels {

  val userWithToken : (Int, String, Option[String],  String) => JsObject =  (id, name, email, token) => Json.obj(
    "id" -> id,
    "name" -> name,
    "email" -> email,
    "token" -> token
  )

  def userJson( id: Int, name: String, email: Option[String]) = {
    Json.obj(
      "id" -> id,
      "name" -> name,
      "email" -> email
    )
  }

  def saveData(id: Int, name: String, musicData: String) = {
    Json.obj(
      "id" -> id,
      "name" -> name,
      "musicData" -> musicData
    )
  }

}
