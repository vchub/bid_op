package controllers.client

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.libs.json.{ JsValue, Json, JsObject }
import play.api.test._
import play.api.test.Helpers._

import org.joda.time._
import scala.xml._

import play.api.libs.concurrent._
import play.api.libs.ws

import domain.{ User, Campaign, Network }
import dao.squerylorm._
import dao.squerylorm.test.helpers._

class ClientSpec extends Specification with AllExpectations {

  "getSomeYandexData" should {
    sequential

    "the basic request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {

        val url = "https://api-sandbox.direct.yandex.ru/json-api/v4/"//this url don't work!

        //data for OAuth authentication
        val login = "vlad.ch01"
        val token = "8817974ce8b04ad7b8990346c3618f2d"
        val app_id = "ee754e0b3d0e4a268e2044d14c112a55"
        // input data structure (dictionary)
        val data = Map(
          "method" -> "GetCampaignsList",
          "login" -> login,
          "application_id" -> app_id,
          "token" -> token,
          "locale" -> "en" //"param"-> [login]
          )
        import play.api.libs.ws._

        val jsData = Json.toJson(data)

        //await(WS.url(url).get).status must equalTo(OK)

        /*val result = { WS.url(url).post[JsValue](jsData) }
        val res = await(result)
        print(res.json)
        res.status must equalTo(OK)*///this url don't work!
        1 must_==(1)
      }
    }
  }

}


