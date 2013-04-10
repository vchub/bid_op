package dao.squschema

import org.specs2.mutable._
import org.specs2.specification._

import play.api.test._
import play.api.test.Helpers._

import play.api.data._
import play.api.data.Forms._
import play.api.Play
import play.api.libs.json.{JsValue, Json, JsObject}

import java.util.Date
import org.joda.time

import scala.io.Source

 import net.liftweb.json._
 import net.liftweb.json.JsonDSL._


case class Winner(id: Long, numbers: List[Int])
case class Lotto(id: Long, winningNumbers: List[Int], winners: List[Winner], drawDate: Option[java.util.Date])

class play_playSpec extends Specification with AllExpectations {

  "lift.json" should {
    "from class to json" in {
      val winners = List(Winner(23, List(2, 45, 34, 23, 3, 5)), Winner(54, List(52, 3, 12, 11, 18, 22)))
      val lotto = Lotto(5, List(2, 45, 34, 23, 7, 5, 3), winners, Some(new Date))
      val json =
            ("id" -> lotto.id) ~
            ("winningNumbers" -> lotto.winningNumbers) ~
            ("draw-date" -> lotto.drawDate.map(_.toString)) ~
            ("winners" ->
              lotto.winners.map { w =>
                (("id" -> w.id) ~
                ("numbers" -> w.numbers))}
            )

      val j_str = compact(render(json))
      //println(j_str)
      val j_value = parse(j_str).transform {
          case JField("draw-date", x) => JField("drawDate", x)
        }
      implicit val formats = DefaultFormats // Brings in default date formats etc.
      val lotto_1 = j_value.extract[Lotto]
      lotto_1.id must_==(5)
      lotto_1.winners.length must_==(2)
      lotto_1.winners(0).numbers(0) must_==(2)
    }
  }


  "xml processing" should {
    sequential
    "load from file" in {
      running(FakeApplication()) {
        val file_name = "test/serializers/report1.xml"
        val xml_str = Source.fromFile(file_name, "utf-8").getLines.mkString
        val node_from_file = scala.xml.XML.loadFile(file_name)
        val node_from_str = scala.xml.XML.loadString(xml_str)
        // get some attributes
        val bannerIDs = (node_from_file\\"@bannerID")
        bannerIDs.length must_==(3)
        (node_from_str\\"@bannerID") === bannerIDs
        // get rows
        val rows = (node_from_str\\"row")
        bannerIDs.length must_==(3)

    }}
  }


  "using play.api.libs.json" should {
    "parse a json string" in {
        // arrange date and formatters
        val cust_fmt = time.format.DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")
        val cust_fmt_no_Z = time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ss.SSS")
        val fmt = time.format.ISODateTimeFormat.dateTime() // standart 8601

        val date: time.DateTime = fmt.parseDateTime("2012-09-19T10:00:00.000+04:00")

        // source Map
        val source = Map(
          "start_date" -> fmt.print(date),
          "end_date" -> fmt.print(date.plusDays(30)),
          "network_campaign_id" -> "2.0",
          "budget" -> "50"
        )
        // sourse json
        val js = """{
          "start_date": "%s",
          "end_date": "%s",
          "network_campaign_id": "y1",
          "budget": 50
        }""".format(fmt.print(date), fmt.print(date.plusDays(30)))

        // make a jnode
        val node = Json.parse(js)
        // extract a field from the jnode
        val d = (node\"start_date").asOpt[String]
        d.get must_==(fmt.print(date))

        // try to create a Campaign from the jnode
        val start_date = (node\"start_date").asOpt[String].map(fmt.parseDateTime(_))
        val end_date = (node\"end_date").asOpt[String].map(fmt.parseDateTime(_))
        val network_campaign_id = (node\"network_campaign_id").asOpt[String].get
        val budget = (node\"budget").asOpt[Double].get

        val c = new Campaign(start_date = start_date.get.toDate, end_date = end_date.get.toDate,
          network_campaign_id = network_campaign_id, budget = budget)
        // check some
        new time.DateTime(c.start_date) must_==(date)
        new time.DateTime(c.end_date) must_==(date.plusDays(30))
        c.budget must_==(50.0)
    }

    /*
    "parse a json string and use Form for validation" in {
      //TODO:
      // arrange date and formatters
      val fmt = time.format.ISODateTimeFormat.dateTime() // standart 8601
      val date0: time.DateTime = fmt.parseDateTime("2012-09-19T10:00:00.000+04:00")

      // sourse json
      val js = """{
        "start_date0": "%s",
        "end_date0": "%s",
        "network_campaign_id": "y1",
        "budget": 50
      }""".format(fmt.print(date0), fmt.print(date0.plusDays(30)))
      // make a jnode
      val node = Json.parse(js)

      // parse params
      val start_date = (node\"start_date").asOpt[String].map(fmt.parseDateTime(_))
      val end_date = (node\"end_date").asOpt[String].map(fmt.parseDateTime(_))
      val network_campaign_id = (node\"network_campaign_id").asOpt[String].get
      val budget = (node\"budget").asOpt[Double].get

      // source Map
      val sourceMap = Map(
        "start_date" -> start_date,
        "end_date" -> end_date,
        "network_campaign_id" -> network_campaign_id,
        "budget" -> budget
      )

      object my_Formatter[Double] with Formatter{

      }
      //make a Form
      val campaignForm = Form(
        mapping(
          "user_id" -> ignored(0L),
          "network_id" -> ignored(0L),
          "network_campaign_id" -> nonEmptyText,
          "start_date" -> date, //sqlDate("yyyy-MM-dd'T'hh:mm:ss.SSSZ"),
          "end_date" -> date, //sqlDate("yyyy-MM-dd'T'hh:mm:ss.SSSZ"),
          "budget" -> of[Double](implicit binder: my_Formatter[Double])
        )(Campaign.apply)(Campaign.unapply)
      )

      val c = campaignForm.bind(sourceMap).get
      // check some
      new time.DateTime(c.start_date) must_==(date0)
      new time.DateTime(c.end_date) must_==(date0.plusDays(30))
      c.budget must_==(50.0)
    }
    */

  }

  "joda.DateTime" should {
    "be created from standart ISO-8601 string" in {
        val cust_fmt = time.format.DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")
        val date: time.DateTime = cust_fmt.parseDateTime("2012-09-19 10:00:00")

        //cust_fmt
        val cust_fmt_no_Z = time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ss.SSS")
        cust_fmt_no_Z.print(date) must_==("2012-09-19T10:00:00.000")

        // standart 8601
        val fmt = time.format.ISODateTimeFormat.dateTime()
        val res = fmt.print(date)
        res must_==("2012-09-19T10:00:00.000+04:00")

        // basic
        val fmt_basic = time.format.ISODateTimeFormat.basicDate()
        fmt_basic.print(date) must_==("20120919")

        // date
        val fmt_date = time.format.ISODateTimeFormat.date()
        fmt_date.print(date) must_==("2012-09-19")

    }


  }

  "Configuration" should {
    sequential
    "be accessed by key" in {
      running(FakeApplication()) {
        val config = Play.current.configuration
        val user = config.getString("vlad.mysql.user")
        //println(config.getString("vlad"))
        user.get must_==("vlad")
    }}
  }

}


