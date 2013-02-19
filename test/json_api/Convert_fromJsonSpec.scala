package json_api

import json_api.Convert._
import serializers._

import org.specs2.mutable._
import org.specs2.specification._
import play.api.libs.json._

class Convert_fromJsonSpec extends Specification with AllExpectations {

  /*------------- User ---------------------------------------------------*/
  "fromJson - User" should {
    sequential

    "take TRUE data" in {
      val data = """
       {"name": "krisp0",
        "password": "123"}"""

      val Some(res) = fromJson[User](Json.parse(data))

      res.name must_== ("krisp0")
      res.password must_== ("123")
    }
  }
}