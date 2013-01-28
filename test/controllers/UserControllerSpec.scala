package controllers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.test._
import play.api.test.Helpers._

import domain.{ User, Campaign, Network }
import dao.squerylorm.test.helpers._

class UserControllerSpec extends Specification with AllExpectations {

  "UserController" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/foo").withHeaders(("password" -> "123")))
        status(res) must equalTo(404)
      }
    }

    "send 404 or 400 on a wrong Password request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val Some(res0) = routeAndCall(FakeRequest(GET, "/user/Coda").withHeaders(("password" -> "12345678")))
        status(res0) must equalTo(404) //NotFound

        val Some(res1) = routeAndCall(FakeRequest(GET, "/user/Coda"))
        status(res1) must equalTo(400) //BadRequest
      }
    }

    "respond User in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val res = routeAndCall(FakeRequest(GET, "/user/Coda").withHeaders(("password" -> "123"))).get

        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("Coda")
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
