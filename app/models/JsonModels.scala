package models

import play.api.libs.json.Json

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object JsonModels {

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
