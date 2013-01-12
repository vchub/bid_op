package controllers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.libs.json.{ JsValue, Json, JsObject }
import play.api.test._
import play.api.test.Helpers._

import org.joda.time._
import scala.xml._

import domain.{ User, Campaign, Network }
import dao.squerylorm._
import dao.squerylorm.test.helpers._

class CampaignControllerSpec extends Specification with AllExpectations {

  "campaigns" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/boum/net/Yandex/camp"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(GET, "/user/Coda/net/yandex/camp"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "respond Campaign in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp"))

        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("y1")
      }
    }
  }

  "campaign" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/boum/net/Yandex/camp/y1"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(GET, "/user/Coda/net/yandex/camp/y1"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y100"))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "respond Campaign in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1")).get

        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("y1")
      }
    }
  }

  "createCampaignPerformance" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/boum/net/Yandex/camp/y1/stats"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(POST, "/user/Coda/net/yandex/camp/y1/stats"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y100/stats"))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "respond status 201(created) and Performance in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        import play.api.libs.json.{ JsValue, Json, JsObject }

        val date_fmt = format.DateTimeFormat.forPattern("yyyy-MM-dd")
        val iso_fmt = format.ISODateTimeFormat.dateTime() // standart 8601
        val start_date: DateTime = iso_fmt.parseDateTime("2012-10-19T00:00:00.000+04:00")
        val date: DateTime = iso_fmt.parseDateTime("2012-10-19T10:00:00.000+04:00")

        val node = Json.toJson(Map(
          "start_date" -> iso_fmt.print(start_date), // in format "yyyy-MM-dd"
          "end_date" -> iso_fmt.print(date), // in standart ISO 8601
          "sum_search" -> "2.0",
          "sum_context" -> "2.5",
          "impress_search" -> "4",
          "impress_context" -> "5",
          "clicks_search" -> "1",
          "clicks_context" -> "2"))

        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/stats")
          .withJsonBody(node))

        status(res) must equalTo(201)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("sum_search")
        //must contain Date.getTime result
        contentAsString(res) must contain(date.getTime.toString)

        //retrieve from DB, check ascending order and in conformance to historyStartDate, historyEndDate
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1", historyStartDate = date.minusMinutes(1),
          historyEndDate = date.plusMinutes(1)).get
        val perfHistory = c.performanceHistory
        perfHistory.length must_== (1)
        val performance = perfHistory(0)
        performance.dateTime must_== (date)
        performance.periodType.id must_!= (0)
      }
    }
  }

  "create Campaign" should {
    sequential

    "send 404 on a wrong User, Network" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/boum/net/Yandex/camp"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(POST, "/user/Coda/net/yandex/camp"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "send BadRequest (400) on empty request body" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val Some(res2) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp"))
        status(res2) must equalTo(400)
      }
    }

    "send BadRequest (400) on malformed json representation of Campaign" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        import com.codahale.jerkson
        import org.joda.time
        //date
        val fmt_date = format.ISODateTimeFormat.date()
        val date: DateTime = fmt_date.parseDateTime("2012-09-19")

        // sourse json
        // no network_campaign_id
        val js = """{
          "start_date": "%s",
          "end_date": "%s",
          "budget": 50
        }""".format(fmt_date.print(date), fmt_date.print(date.plusDays(30)))
        val node = Json.parse(js)
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp")
          .withJsonBody(node))
        status(res) must equalTo(400)
      }
    }

    "respond status 201(created) and Campaign in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        import com.codahale.jerkson
        import org.joda.time

        //date
        val iso_fmt = format.ISODateTimeFormat.dateTime() // standart 8601
        val date: DateTime = iso_fmt.parseDateTime("2012-10-19T11:00:00.000+04:00")

        // sourse json
        val js = """{
          "start_date": "%s",
          "end_date": "%s",
          "network_campaign_id": "y100",
          "daily_budget": 50,
          "_login": "",
          "_token": ""
        }""".format(iso_fmt.print(date), iso_fmt.print(date.plusDays(30)))
        val node = Json.parse(js)

        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp")
          .withJsonBody(node))

        status(res) must equalTo(201)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("budget")
        //must contain Date.getTime result
        contentAsString(res) must contain(date.getTime.toString)

        //retrieve from DB, check ascending order and in conformance to historyStartDate, historyEndDate
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y100", historyStartDate = date.minusMinutes(1),
          historyEndDate = date.plusMinutes(2)).get
        c.id must_!= (0)
        c.network_campaign_id must_== ("y100")
        c.budgetHistory.length must_== (1)
        c.endDate must_== (Some(date.plusDays(30)))
        c.budget must_== (Some(50.0))
      }
    }
  }

  "createXmlReport" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/boum/net/Yandex/camp/y1/reports"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(POST, "/user/Coda/net/yandex/camp/y1/reports"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y100/reports"))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "send BadRequest (400) on empty request body" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/reports"))
        status(res) must equalTo(400)
      }
    }

    def change_in_node(node: NodeSeq, pattern: String, new_content: String): NodeSeq = {
      val buf = node.toString
      val res = buf.replaceFirst(pattern, new_content)
      scala.xml.XML.loadString(res)
    }

    def get_FakeRequest_POST_xml(uri: String, node: NodeSeq) = {
      FakeRequest(POST, uri, FakeHeaders(Map("Content-Type" -> Seq("application/xml"))),
        AnyContentAsXml(node), "localhost")
    }

    "send BadRequest (400) on malformed or not complete (dates, sums and other fields) xml" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // get test data
        val file_name = "test/serializers/yandex/reports/report1.xml"
        val node = xml.XML.loadFile(file_name)
        // create a copy w/o end_date
        val bad_node = <report> { (node \ "stat") }</report>
        // prepare and make request
        val Some(res) = routeAndCall(get_FakeRequest_POST_xml("/user/Coda/net/Yandex/camp/y1/reports",
          bad_node))
        status(res) must equalTo(400)

        // create a copy w/o sum_search
        val pattern = "sum_context=\".*\""
        val bad_node_1 = change_in_node(node, pattern, "")
        // make request
        val Some(res1) = routeAndCall(get_FakeRequest_POST_xml("/user/Coda/net/Yandex/camp/y1/reports",
          bad_node))
        status(res1) must equalTo(400)
      }
    }

    "respond status 201(created) and Campaign in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // get test data
        val file_name = "test/serializers/yandex/reports/report1.xml"
        val node = xml.XML.loadFile(file_name)
        val Some(res) = routeAndCall(get_FakeRequest_POST_xml("/user/Coda/net/Yandex/camp/y1/reports",
          node))

        status(res) must equalTo(201)

        // check if saved in DB
        val dao = new SquerylDao
        //date
        val iso_fmt = format.ISODateTimeFormat.dateTime() // standart 8601
        val date: DateTime = iso_fmt.parseDateTime("2012-10-20T00:00:00.000+04:00")

        val campaign = dao.getCampaign("Coda", "Yandex", "y1", historyStartDate = date.minusDays(2),
          historyEndDate = date.plusMinutes(2)).get
        // it should be 4 old and 3 new BannerPhrase
        campaign.bannerPhrases.length must_== (7)

        // get one of the new BannerPhrases
        val bps = campaign.bannerPhrases filter (_.banner.get.network_banner_id == "123456")
        //only BannerPhrase is created
        bps.length must_== (1)
        val created_bp = bps.head
        // check phrase is created
        created_bp.phrase.get.network_phrase_id must_== ("538205157")

        // check 1 Performance is created
        created_bp.performanceHistory.length must_== (1)
        val performance = created_bp.performanceHistory(0)
        performance.clicks_search must_== (100)
        performance.dateTime must_== (date)
      }
    }
  }

  "createBannerReport" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/boum/net/Yandex/camp/y1/bannerreports"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(POST, "/user/Coda/net/yandex/camp/y1/bannerreports"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y100/bannerreports"))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "send BadRequest (400) on empty request body" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/bannerreports"))
        status(res) must equalTo(400)
      }
    }

    "send BadRequest (400) on malformed json representation of Campaign" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        import com.codahale.jerkson
        import org.joda.time
        //date
        val fmt_date = format.ISODateTimeFormat.date()
        val date: DateTime = fmt_date.parseDateTime("2012-09-19")

        // sourse json
        // no network_campaign_id
        val js = """{
          "start_date": "%s",
          "end_date": "%s",
          "budget": 50
        }""".format(fmt_date.print(date), fmt_date.print(date.plusDays(30)))
        val node = Json.parse(js)
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/bannerreports")
          .withJsonBody(node))
        status(res) must equalTo(400)
      }
    }

    "respond status 201(created) and true in json" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        import com.codahale.jerkson
        import org.joda.time

        //date
        //val iso_fmt = format.ISODateTimeFormat.dateTime() // standart 8601
        //val date: DateTime = iso_fmt.parseDateTime("2012-10-19T11:00:00.000+04:00")

        // sourse json
        val file_name = "test/serializers/yandex/reports/bannerReport0.json"
        val js = io.Source.fromFile(file_name, "utf-8").getLines.mkString
        val node = Json.parse(js)

        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/bannerreports")
          .withJsonBody(node))

        status(res) must equalTo(201)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("true")

        //retrieve from DB, check ascending order and in conformance to historyStartDate, historyEndDate
        val dao = new SquerylDao
        // Histories are created w/ dateTime = Now
        val date = new DateTime
        val c = dao.getCampaign("Coda", "Yandex", "y1", historyStartDate = date.minusMinutes(1),
          historyEndDate = date.plusMinutes(2)).get

        // get one of the new BannerPhrases
        val bps = c.bannerPhrases filter (_.banner.get.network_banner_id == "11")
        //only BannerPhrase is created
        bps.length must_== (2)

        val created_bp = bps.head
        // check phrase is created
        created_bp.phrase.get.network_phrase_id must_== ("22")

        // check 1 NetAdvisedBids is created
        created_bp.netAdvisedBidsHistory.length must_== (1)
        val record = created_bp.netAdvisedBidsHistory(0)
        record.a must_== (1.1)
        record.b must_== (2.0)
        (record.dateTime.isAfter(date))

        // Check that ActualBidHistory record is created
        created_bp.actualBidHistory.length must_== (1)
        val ab = created_bp.actualBidHistory(0)
        ab.elem must_== (1.0)
        (ab.dateTime.isAfter(date))
      }
    }
  }

  "recommendations" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/boum/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T11:00:00.000+04:00")))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y100/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T12:00:00.000+04:00")))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    /** creates Permutations which creates Recommendations in DB */
    def createPermutaionRecommendation(c: Campaign, dateTime: DateTime) = {
      // create Permutation Map[BannerPhrase, Position]
      val permutation_map = (for (b <- c.bannerPhrases; i <- 0 to c.bannerPhrases.length)
        yield (b, domain.po.Position(i))).toMap

      // create domain.Permutation
      val permutation = new domain.po.Permutation(0, dateTime = dateTime, permutation = permutation_map)
      // create dummy Curve
      val curve = new domain.po.Curve(0, 1, 1, 1, 1, dateTime, None)
      // save Permutation Recommendation and RecommendationChangeDate to DB
      val dao = new SquerylDao
      dao.createPermutaionRecommendation(permutation, c, curve)
    }

    "respond status 304 (NOT_MODIFIED) if recommendations are not modified since" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head

        // set Date
        val date_str = "2012-10-19T13:00:10.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create 2 Recommendations entity with early and late start_date
        createPermutaionRecommendation(campaign, date.minusMinutes(1))
        createPermutaionRecommendation(campaign, date.plusMinutes(1))

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", date_str))).get
        // check status
        status(res) must equalTo(NOT_MODIFIED)
      }
    }

    "respond recommendations" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head

        // set Date
        val date_str = "2012-09-19T01:00:00.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create Recommendation entity with early start_date
        createPermutaionRecommendation(campaign, date.plusMinutes(1))

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", fmt.print(date)))).get

        // check status
        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        // check result
        val content = contentAsString(res)
        content must not contain ("regionID") // network_region_id
        // create serializers.Recommendation from result
        import com.codahale.jerkson.Json
        val rec = Json.parse[List[serializers.PhrasePriceInfo]](content)
        rec.length must_== (4)
        rec(0).CampaignID must_== (1)
        
        /* Changed!!!
        val rec = Json.parse[serializers.Recommendation](content)
        rec.param.length must_== (4)
        rec.param(0).CampaignID must_== (1)*/
      }
    }
  }

}


