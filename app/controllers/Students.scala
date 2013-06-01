package controllers

import play.api.mvc._
import play.api.libs.json._

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
object Students extends Controller {

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

    val students = Json.obj(
      "students" -> Json.arr(
        Json.obj(
          "id" -> 1,
          "name" -> "Johnny",
          "token" -> "12345"
        ),
        Json.obj(
          "id" -> 2,
          "name" -> "Suzie",
          "token" -> "12345"
        )
      )
    )

    Ok(students)
  }

  /** Updates a teacher */
  def put(id: Long) = Action {
     val teacher = Json.obj(
       "id" -> 2,
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

  def getTeachers(id: Long) = Action {
    val teachers = Json.obj(
      "teachers" -> Json.arr(1)
    )
    Ok(teachers)
  }

  def putTeachers(id: Long) = Action {
    val teachers = Json.obj(
      "teachers" -> Json.arr(1,2)
    )
    Ok(teachers)
  }

  def delTeachers(studentId: Long, teacherId: Long) = Action {
    val teachers = Json.obj(
      "teachers" -> Json.arr(1)
    )
    Ok(teachers)
  }

}
