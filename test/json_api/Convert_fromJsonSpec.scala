package json_api

import json_api.Convert._
import serializers._

import org.specs2.mutable._
import org.specs2.specification._

import play.api.libs.json._

import org.joda.time.format

class Convert_fromJsonSpec extends Specification with AllExpectations {

  val iso_fmt = format.ISODateTimeFormat.dateTime()

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

  /*------------- Campaign ---------------------------------------------------*/
  "fromJson - Campaign" should {
    sequential

    "take TRUE data" in {
      val date = iso_fmt.parseDateTime("2013-01-01T12:00:00.000+04:00")

      val data = """
       {"start_date": %d,
        "end_date": %d,
        "_login": "krisp0",
        "_token": "123",
        "network_campaign_id": "100",        
        "daily_budget": 50.0}""".format(date.getMillis(), date.plusDays(1).getMillis())

      val Some(res) = fromJson[Campaign](Json.parse(data))

      res._login must_== ("krisp0")
      res._token must_== ("123")
      res.start_date must_== (date)
    }
  }
  /*------------- List[Campaign] ---------------------------------------------------*/
  "fromJson - List[Campaign]" should {
    sequential

    "take TRUE data" in {
      val date = iso_fmt.parseDateTime("2013-01-01T12:00:00.000+04:00")

      val data = """[
       {"start_date": %d,
        "end_date": %d,
        "_login": "krisp0",
        "_token": "123",
        "network_campaign_id": "100",        
        "daily_budget": 50.0},
       {"start_date": %d,
        "end_date": %d,
        "_login": "krisp0",
        "_token": "123",
        "network_campaign_id": "100",        
        "daily_budget": 50.0}
       ]""".format(date.getMillis(), date.plusDays(1).getMillis(), date.getMillis(), date.plusDays(1).getMillis())

      val Some(res) = fromJson[List[Campaign]](Json.parse(data))

      res.head._login must_== ("krisp0")
      res.head._token must_== ("123")
      res.head.start_date must_== (date)
      res.last.daily_budget must_== (50.0)
    }
  }
}