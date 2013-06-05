package controllers

import play.api.mvc.{Action, Result, Request, BodyParsers, Results}
import play.api.libs.json.{JsValue, JsError, Reads}



import scalaz.{Show, Semigroup, Validation}
import scalaz.syntax.applicative._
import scalaz.syntax.validation._
import javax.sql.DataSource

trait JsErrorZ {
  implicit def jsErrorSemigroup: Semigroup[JsError] = new Semigroup[JsError] {
    def append(s1: JsError, s2: => JsError): JsError = s1 ++ s2
  }
  implicit def jsShow: Show[JsError] = Show.shows(_.toString)

}



trait Data[I,O] {
  def reads: Reads[I]
  def validation: I => Validation[JsError, I]
  def toJson: O => JsValue
}


/**
 * User: travis.stevens@gaiam.com
 * Date: 6/5/13
 */
trait JsonInput[I,O] extends Data[I,O] with Results {

  def slickBehavior: I => O
  def validation: I => Validation[JsError, I]

  def action = (ds: DataSource) => Action[JsValue](BodyParsers.parse.json)(doRequest(_)(ds))

  def doRequest(request: Request[JsValue])(implicit ds: DataSource): Result = {
    request.body.validate(reads).map(i => {
      validation(i).map(slickBehavior).map(toJson).fold(
        errors => BadRequest(JsError.toFlatJson(errors)),
        result => Ok(result)
      )
    }).recoverTotal{
      e => BadRequest(JsError.toFlatJson(e))
    }
  }
}

trait JsonValInput[I,O,V] extends Data[I,O] with Results with JsErrorZ {
  def action = (v: V, ds: DataSource) => Action[JsValue](BodyParsers.parse.json)(doRequest(_, v)(ds))

  def slickBehavior: (I,V) => O
  def validationForV: V => Validation[JsError, V]

  private def allValidation(i: I, v: V) : Validation[JsError, (I,V)] = {
    (validation(i) |@| validationForV(v) ) { (_,_) }
  }

  def doRequest(request: Request[JsValue], v: V)(implicit ds: DataSource): Result = {
    request.body.validate(reads).map(i => {
      allValidation(i, v).map(slickBehavior.tupled).map(toJson).fold(
        errors => BadRequest(JsError.toFlatJson(errors)),
        result => Ok(result)
      )
    }).recoverTotal{
      e => BadRequest(JsError.toFlatJson(e))
    }
  }

}
