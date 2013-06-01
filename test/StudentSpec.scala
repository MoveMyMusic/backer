import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
class StudentSpec extends Specification {

  "student" should {
    "can get all students" in {
      running(FakeApplication()) {
        val teachers = route(FakeRequest(GET, "/students")).get

        status(teachers) must equalTo(OK)
      }
    }

    "can post, get, put and a student and add and remove to a teacher" in {
      running(FakeApplication()) {

        val post = FakeRequest(POST, "/students")
        post.withBody(
          """
            |{
            |   "name" : "Test User",
            |   "password" : "password123"
            |}
          """.stripMargin)

        val teacherPost = route(post).get
        status(teacherPost) must equalTo(OK)

        val student = route(FakeRequest(GET, "/students/7")).get
        status(student) must equalTo(OK)

        val put = FakeRequest(PUT, "/students/7")
        put.withBody(
          """
            | { password : "password1234" }
          """.stripMargin
        )

        val teacherMod = route(put).get
        status(teacherMod) must equalTo(OK)

        val getStudentTeachers = route(FakeRequest(GET, "/students/7/teachers")).get
        status(getStudentTeachers) must equalTo(OK)

        val addTeacherRequest = FakeRequest(PUT, "/students/7/teachers")
        addTeacherRequest.withBody(
          """
            |{ "teacherId" : "1" }
          """.stripMargin)

        val delTeacherRequest = FakeRequest(DELETE, "/students/7/teachers/1")

      }

    }
  }



}
