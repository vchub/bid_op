package json_api

import json_api.Convert._
import serializers._

import org.specs2.mutable._
import org.specs2.specification._

import play.api.libs.json._

import org.joda.time.format
import org.joda.time._

class Convert_toJsonSpec extends Specification with AllExpectations {

  val iso_fmt = format.ISODateTimeFormat.dateTime()

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

  /*------------- Campaign ---------------------------------------------------*/
  "toJson - Campaign" should {
    sequential

    "take TRUE data" in {
      val date = iso_fmt.parseDateTime("2013-01-01T12:00:00.000+04:00")

      val data = Campaign(_login = "krisp0", _token = "123", start_date = date)

      val res = toJson[Campaign](data)

      res \ "_login" must_== (JsString("krisp0"))
      res \ "_token" must_== (JsString("123"))
      (res \ "start_date").as[DateTime] must_== (date)
    }
  }
  /*------------- List[Campaign] ---------------------------------------------------*/
  "toJson - List[Campaign]" should {
    sequential

    "take TRUE data" in {
      val date = iso_fmt.parseDateTime("2013-01-01T12:00:00.000+04:00")

      val data = List(
        Campaign(_login = "krisp0", _token = "123", start_date = date),
        Campaign(_login = "0psikr", _token = "321", start_date = date))

      val res = toJson[List[Campaign]](data)

      res \\ "_login" map (_.as[String]) must_== (List("krisp0", "0psikr"))
      res \\ "_token" map (_.as[String]) must_== (List("123", "321"))
      res \\ "start_date" map (_.as[DateTime]) must_== (List(date, date))
    }
  }

  /*------------- Performance ---------------------------------------------------*/
  "toJson - Performance" should {
    sequential

    "take TRUE data" in {
      val date = iso_fmt.parseDateTime("2013-01-01T12:00:00.000+04:00")

      val data = Performance(
        start_date = date,
        end_date = date,
        sum_search = 10.0,
        sum_context = 20.0,
        impress_search = 10,
        impress_context = 20,
        clicks_search = 100,
        clicks_context = 200)

      val res = toJson[Performance](data)

      (res \ "start_date").as[DateTime] must_== (date)
      res \ "sum_search" must_== (JsNumber(10.0))
      res \ "impress_context" must_== (JsNumber(20))
    }
  }

  /*------------- PhrasePriceInfo ---------------------------------------------------*/
  "toJson - PhrasePriceInfo" should {
    sequential

    "take TRUE data" in {
      val data = PhrasePriceInfo(
        PhraseID = 1,
        BannerID = 2,
        CampaignID = 3,
        Price = 1.0)

      val res = toJson[PhrasePriceInfo](data)
      res \ "PhraseID" must_== (JsNumber(1))
      res \ "Price" must_== (JsNumber(1.0))      
    }
  }
  /*------------- List[PhrasePriceInfo] ---------------------------------------------------*/
  "toJson - List[PhrasePriceInfo]" should {
    sequential

    "take TRUE data" in {
      val data = List(
        PhrasePriceInfo(PhraseID = 1, BannerID = 2, CampaignID = 3, Price = 1.1),
        PhrasePriceInfo(PhraseID = 2, BannerID = 3, CampaignID = 4, Price = 2.2))

      val res = toJson[List[PhrasePriceInfo]](data)
      res \\ "PhraseID" map (_.as[Int]) must_== (List(1, 2))
      res \\ "Price" must_== (List(JsNumber(1.1), JsNumber(2.2)))      
    }
  }
}