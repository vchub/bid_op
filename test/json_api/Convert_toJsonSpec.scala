package json_api

import json_api.Convert._
import serializers._

import org.specs2.mutable._
import org.specs2.specification._
import play.api.libs.json._

class Convert_toJsonSpec extends Specification with AllExpectations {

  /*------------- User ---------------------------------------------------*/
  "toJson - User" should {
    sequential

    "take TRUE data" in {
      val data = User("krisp0", "123")

      val res = toJson[User](data)

      res \ "name" must_== (JsString("krisp0"))
      res \ "password" must_== (JsString("123"))
    }
  }
}