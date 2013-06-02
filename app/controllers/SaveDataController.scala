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
 * Date: 6/2/13
 */
object SaveDataController extends Controller {

  def post(studentId: Int) = Action { request =>

    val input = (
      (__ \ 'name).read[String] and (__ \ 'musicData).read[String]
    ) tupled

    request.body.asJson.map( { json =>
      json.validate[(String, String)](input).map {
        case (name, data) => {
          Database.forDataSource(DB.getDataSource()) withSession {
//            MusicData.doInsert(name, data, studentId)
            Ok("ok")
          }
        }
      }.recoverTotal{
        e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
      }
    }).getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  def put(dataId: Int) = {
    val input = (
      (__ \ 'musicData).read[Option[String]] and (__ \ 'name).read[Option[String]] tupled
    )


  }

}
