package controllers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.test._
import play.api.test.Helpers._

import dao.squschema._
import dao.squschema.test.helpers._

class UserControllerSpec extends Specification with AllExpectations {

  "UserController" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.running_FakeApplication() {
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/foo/net/bar/camp/0"))
        status(res) must equalTo(404)
      }
    }

    "respond List[Campaign] in json" in {
      TestDB_0.running_FakeApplication() {
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1")).get

        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("y1")
      }
    }


    /*
    "render an empty form on index" in {
      running(FakeApplication()) {
        val home = routeAndCall(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
      }
    }

    "send BadRequest on form error" in {
      running(FakeApplication()) {
        val home = routeAndCall(FakeRequest(GET, "/hello?name=Bob&repeat=xx")).get

        status(home) must equalTo(BAD_REQUEST)
        contentType(home) must beSome.which(_ == "text/html")
      }
    }
    */


  }

}
