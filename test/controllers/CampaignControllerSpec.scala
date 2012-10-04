package test.controllers

import org.specs2.mutable._
import org.specs2.specification._

import play.api.mvc._
import play.api._
import play.api.libs.json.{JsValue, Json, JsObject}

import play.api.test._
import play.api.test.Helpers._

import java.util.Date
import org.joda.time._
import scala.xml._

import models.db.schema.helpers._
import models.db.schema._


class CampaignControllerSpec extends Specification with AllExpectations {

  "campaigns" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.running_FakeApplication() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/boum/net/Yandex/camp"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(GET, "/user/Coda/net/yandex/camp"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "respond Campaign in json" in {
      TestDB_0.running_FakeApplication() {
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
      TestDB_0.running_FakeApplication() {
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

  "create_TimeSlot" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.running_FakeApplication() {
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

    "respond status 201(created) and TimeSlot in json" in {
      TestDB_0.running_FakeApplication() {
        import play.api.libs.json.{JsValue, Json, JsObject}
        //import com.codahale.jerkson

        val cust_fmt = format.DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")
        val date: DateTime = cust_fmt.parseDateTime("2012-09-19 10:00:00")
        val cust_fmt_no_Z = format.DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ss.SSS")

        //cust_fmt

        val node = Json.toJson(Map(
          "start_date" -> cust_fmt_no_Z.print(date),
          "end_date" -> cust_fmt_no_Z.print(date.plusMinutes(30)),
          //"start_date" -> "2012-09-19 10:00:00",
          //"end_date" -> "2012-09-19 10:30:00",
          "sum_search" -> "2.0",
          "sum_context" -> "2.5",
          "impress_search" -> "4",
          "impress_context" -> "5",
          "clicks_search" -> "1",
          "clicks_context" -> "2"
        ))

        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/stats")
          .withJsonBody(node))

        status(res) must equalTo(201)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("sum_search")
        //must contain Date.getTime result
        //TODO: date is stored in DB without Time Zone! Do we need to fix it?
        contentAsString(res) must contain(date.plusHours(4).toDate.getTime.toString)
      }
    }
  }

  "create Campaign" should {
    sequential

    "send 404 on a wrong User, Network" in {
      TestDB_0.running_FakeApplication() {
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/boum/net/Yandex/camp"))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(POST, "/user/Coda/net/yandex/camp"))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))
      }
    }

    "send BadRequest (400) on empty request body" in {
      TestDB_0.running_FakeApplication() {
        val Some(res2) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp"))
        status(res2) must equalTo(400)
      }
    }

    "send BadRequest (400) on malformed json representation of Campaign" in {
      TestDB_0.running_FakeApplication() {
        import com.codahale.jerkson
        import org.joda.time
        //date
        val fmt_date = time.format.ISODateTimeFormat.date()
        val date: DateTime = fmt_date.parseDateTime("2012-09-19")

        // sourse json
        // not network_campaign_id
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
      TestDB_0.running_FakeApplication() {
        import com.codahale.jerkson
        import org.joda.time

        //date
        val fmt_date = time.format.ISODateTimeFormat.date()
        val date: DateTime = fmt_date.parseDateTime("2012-09-19")

        // sourse json
        val js = """{
          "start_date": "%s",
          "end_date": "%s",
          "network_campaign_id": "y100",
          "budget": 50
        }""".format(fmt_date.print(date), fmt_date.print(date.plusDays(30)))
        val node = Json.parse(js)

        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp")
          .withJsonBody(node))

        status(res) must equalTo(201)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("budget")
        contentAsString(res) must contain(date.plusHours(4).toDate.getTime.toString)
      }
    }
  }

  "create Report" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.running_FakeApplication() {
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
      TestDB_0.running_FakeApplication() {
        val Some(res) = routeAndCall(FakeRequest(POST, "/user/Coda/net/Yandex/camp/y1/reports"))
        status(res) must equalTo(400)
      }
    }

    def change_in_node(node: NodeSeq, pattern: String, new_content: String): NodeSeq = {
      val buf = node.toString
      val res = buf.replaceFirst(pattern, new_content)
      xml.XML.loadString(res)
    }

    def get_FakeRequest_POST_xml(uri: String, node: NodeSeq) = {
      FakeRequest(POST, uri, FakeHeaders(Map("Content-Type" -> Seq("application/xml"))),
          AnyContentAsXml(node), "localhost")
    }

    "send BadRequest (400) on malformed or not complete (dates, sums and other fields) xml" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        // get test data
        val file_name = "test/models/db/schema/xml/report1.xml"
        val node = scala.xml.XML.loadFile(file_name)
        // create a copy w/o end_date
        val bad_node = <report> {(node\"stat")}</report>
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
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        // get test data
        val file_name = "test/models/db/schema/xml/report1.xml"
        val node = scala.xml.XML.loadFile(file_name)
        val Some(res) = routeAndCall(get_FakeRequest_POST_xml("/user/Coda/net/Yandex/camp/y1/reports",
            node))

        status(res) must equalTo(201)

        // check out new entities
        // from the row 1
        val banner = Banner.select(network_banner_id = "123456").head
        banner.id must_!=(0)
        banner.network_banner_id must_==("123456")
        // from the row 2
        val region = Region.select(network_region_id = "2").head
        region.id must_!=(0)
        region.network_region_id must_==("2")
      }
    }
  }


  "recommendations" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_0.running_FakeApplication() {
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

    "respond status 304 (NOT_MODIFIED) if recommendations are not modified since" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head

        // set Date
        val date_str = "2012-09-19T13:00:00.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create Recommendation entity with early start_date
        import org.squeryl.PrimitiveTypeMode._
        inTransaction {
          campaign.recommendations.associate(Recommendation(start_date =
            date.plusMinutes(1).toDate))
        }

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", date_str))).get
        // check status
        status(res) must equalTo(NOT_MODIFIED)
      }
    }

    "respond recommendations" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head

        // set Date
        //val date_str = "2012-09-19T00:01:00.000+04:00"
        val date_str = "2012-09-19T00:00:00.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create Recommendation entity with early start_date
        /*
        import org.squeryl.PrimitiveTypeMode._
        inTransaction {
          campaign.recommendations.associate(Recommendation(start_date = date.minusHours(5).toDate))
        }
        */
        (new Recommendation(campaign.id, date.toDate)).put

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", fmt.print(date.minusMinutes(1)) ))).get

        // check how model works
        campaign.recommendations_changed_since(date.minusMinutes(10).toDate) must_==(true)
        campaign.recommendations_changed_since(date.plusMinutes(10).toDate) must_==(false)

        // check status
        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain("7") // network_region_id
        contentAsString(res) must contain("regionID") // network_region_id
      }
    }

  }


}
