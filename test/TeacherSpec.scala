import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._

/**
 * User: travis.stevens@gaiam.com
 * Date: 6/1/13
 */
class TeacherSpec extends Specification {

  "Teacher" should {
    "can get all teachers" in {
      running(FakeApplication()) {
        val teachers = route(FakeRequest(GET, "/teachers")).get

        status(teachers) must equalTo(OK)
      }
    }

    "can post and get one teacher" in {
      running(FakeApplication()) {
        val post = FakeRequest(POST, "/teachers")
        post.withBody(
          """
            |{
            |   "name" : "Test User",
            |   "password" : "password123"
            |}
          """.stripMargin)

        val teacherPost = route(post).get
        status(teacherPost) must equalTo(OK)

        val teacherGet = route(FakeRequest(GET, "/teachers/7")).get
        status(teacherGet) must equalTo(OK)

        val put = FakeRequest(PUT, "/teachers/7")
        put.withBody(
          """
            | { password : "password1234" }
          """.stripMargin
        )

        val teacherMod = route(put).get
        status(teacherMod) must equalTo(OK)
      }
    }

  }

}
