package json_api

import json_api.Convert._
import serializers._
import serializers.yandex._

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

  /*------------- Performance ---------------------------------------------------*/
  "fromJson - Performance" should {
    sequential

    "take TRUE data" in {
      val date = iso_fmt.parseDateTime("2013-01-01T12:00:00.000+04:00")

      val data = """
       {"start_date": %d,
        "end_date": %d,
        "sum_search": 100.1,
        "sum_context": 99.9,
        "impress_search": 51,        
        "impress_context": 49,
        "clicks_search": 101,        
        "clicks_context": 99
        }""".format(date.getMillis(), date.plusDays(1).getMillis())

      val Some(res) = fromJson[Performance](Json.parse(data))

      res.sum_search must_== (100.1)
      res.impress_context must_== (49)
      res.start_date must_== (date)
    }
  }

  /*------------- BannerInfo ---------------------------------------------------*/
  "fromJson - BannerInfo" should {
    sequential

    "take wrong data null" in {
      val data = JsNull
      fromJson[BannerInfo](data) must_== (None)
    }

    "take WRONG data" in {
      /* wrong data */
      val data = """
      {"BannerID": "100",
        "Text": "text1"
        }"""

      fromJson[BannerInfo](Json.parse(data)) must_== (None)
    }

    "take TRUE data" in {
      val file_name = "test/json_api/bannerInfo.json"
      val data = io.Source.fromFile(file_name, "utf-8").getLines.mkString

      val Some(res) = fromJson[BannerInfo](Json.parse(data))

      res.BannerID must_== (11)
      res.Text must_== ("some")
      res.Geo must_== ("12, 11")
      res.Phrases.length must_== (2)
      res.Phrases.head.PhraseID must_== (22)
      res.Phrases.head.Max must_== (2.0)
      res.Phrases.head.AutoBroker must_== ("Yes")
    }
  }

  /*------------- List[BannerInfo] ---------------------------------------------------*/
  "fromJson - List[BannerInfo]" should {
    sequential

    "take wrong data null" in {
      val data = JsNull
      fromJson[List[BannerInfo]](data) must_== (None)
    }

    "take WRONG data" in {
      /* wrong data */
      val data = """[
      {"BannerID": "100",
        "Text": "text1"
        }]"""

      fromJson[List[BannerInfo]](Json.parse(data)) must_== (None)
    }

    "take TRUE data" in {
      val file_name = "test/json_api/bannerInfoList.json"
      val data = io.Source.fromFile(file_name, "utf-8").getLines.mkString

      val Some(res) = fromJson[List[BannerInfo]](Json.parse(data))
      res.length must_== (2)

      res.head.BannerID must_== (11)
      res.head.Text must_== ("some")
      res.head.Geo must_== ("12, 11")
      res.head.Phrases.length must_== (2)
      res.head.Phrases.head.PhraseID must_== (22)
      res.head.Phrases.head.Max must_== (2.0)
      res.head.Phrases.head.AutoBroker must_== ("Yes")
    }
  }

  /*------------- GetBannersStatResponse ---------------------------------------------------*/
  "fromJson - GetBannersStatResponse" should {
    sequential

    "take wrong data null" in {
      val data = JsNull
      fromJson[GetBannersStatResponse](data) must_== (None)
    }

    "take TRUE data" in {
      import java.text._
      import org.joda.time._
      val date = new SimpleDateFormat("yyyy-MM-dd").parse("2013-01-01")

      val file_name = "test/json_api/getBannersStatResponse.json"
      val data = io.Source.fromFile(file_name, "utf-8").getLines.mkString

      val Some(res) = fromJson[GetBannersStatResponse](Json.parse(data))

      res.CampaignID must_== (10)
      res.StartDate must_== ("2013-01-01")
      res.EndDate must_== ("2013-01-01")
      res.Stat.length must_== (3)
      res.Stat.head.BannerID must_== (11)
      res.Stat.head.Phrase must_== ("some")
      res.Stat.head.PhraseID must_== (1)
      res.Stat.head.Clicks must_== (50)
      res.Stat.head.Shows must_== (550)
      res.Stat.head.Sum must_== (12.3)
      res.Stat.head.StatDate must_== (new DateTime(date))
    }
  }
}