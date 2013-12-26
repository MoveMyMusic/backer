package controllers

import play.api.mvc.{Action, Result, Request, BodyParsers, Results}
import play.api.libs.json.{JsValue, JsError, Reads}
import scala.slick.session.Session

/**
 * I: the input derived from Json
 * O: The output from the insert used to feed the Json result
 */
trait JsonDb[I,O] {
  def reads: Reads[I]
  def toJson: O => JsValue
  def session: Session
}

/** jsonInsert trait creation.  Returns a ReaderMonad that expects a session as input. */
object JsonInsert {
  def apply[I,O](dataReads: Reads[I])(slickAction: I => Session => O)(oToJson: O => JsValue) =
    scalaz.Reader[Session, JsonInsert[I,O]]( slickSession => new JsonInsert[I,O] {
      val reads = dataReads
      val toJson = oToJson
      val slickBehavior = slickAction
      val session = slickSession
    })
}

/**
 *  Trait defining behavior to take jason, insert it using slick and return json
 */
trait JsonInsert[I,O] extends JsonDb[I,O] with Results {

  def slickBehavior: I => Session => O

  def action : Action[JsValue] = Action[JsValue](BodyParsers.parse.json)(doRequest(_))

  def doRequest(request: Request[JsValue]): Result = {
  request.body.validate(reads).map(slickBehavior(_)(session)).map(toJson).fold(
      errors => BadRequest(JsError.toFlatJson(errors)),
      result => Ok(result)
    )
  }
}

/*
 *Creates an instance of JsonUpdate
 */
object JsonUpdate {
  def apply[I,O,V](dataReads: Reads[I])(slickAction: I => (V,Session) => O)(oToJson: O => JsValue) =
    scalaz.Reader[(V,Session), JsonUpdate[I,O,V]]( slickSession => new JsonUpdate[I,O,V] {
      val reads = dataReads
      val toJson = oToJson
      val slickBehavior = slickAction
      val session = slickSession._2
      val v = slickSession._1
    })
}

/**
 * Trait defining behavior to take json and value that identifies a db object in order to update it and return json.
 */
trait JsonUpdate[I,O, V] extends JsonDb[I,O] with Results {
  def slickBehavior: I => (V, Session) => O
  def v: V

  def action: Action[JsValue] = Action[JsValue](BodyParsers.parse.json){ request =>
    request.body.validate(reads).map(slickBehavior(_)(v, session)).map(toJson).fold(
      errors => BadRequest(JsError.toFlatJson(errors)),
      result => Ok(result)
    )
  }
}



