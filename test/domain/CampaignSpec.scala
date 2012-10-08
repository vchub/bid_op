package domain

import org.specs2.mutable._
import org.specs2.specification._

import java.util.Date
import java.text.SimpleDateFormat
import org.joda.time._

import play.api.test._
import play.api.test.Helpers._

import scala.xml._

import domain.dao.helpers._
import domain.dao.AppSchema;
import domain.dao.AppSchemaFactory
import domain.serializing.helpers.YandexXmlReportHelper

class CampaignSpec extends Specification with AllExpectations {

  "Campaign Entity" should {
    sequential

    "be got by id" in {
      TestDB_0.running_FakeApplication() {
        val camp = Campaign get_by_id 1
        camp.id must be_==(1)
    }}

    "be put in DB" in {
      TestDB_0.running_FakeApplication() {
        val user = (new User("Coda--unusual name for user")).put
        val network = (new Network("Yandex but another Yandex though")).put
        val camp = Campaign(network_campaign_id = "y44", start_date = new Date, budget = 100)
        camp.user_id = user.id
        camp.network_id = network.id

        camp.id must be_==(0)
        val res :Campaign = camp.put
        res.id must_!=(0L)
        res.network_id must_==(network.id)
    }}
  }

  "select(user_name, network_name, network_campaign_id)" should {
    sequential
    "get particular Campaign for given names and id" in {
      TestDB_0.running_FakeApplication() {
        val c = Campaign.select("Coda", "Yandex", "y1")
        c.length must_==(1)
        c.head.network_campaign_id must be_==("y1")
    }}
  }

  "select(User, Network, network_campaign_id)" should {
    sequential
    "get particular Campaign for Network and network_campaign_id" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val network = Network.select("Yandex").head
        val c = Campaign.select(user = user, network = network, network_campaign_id = "y1")
        c.length must_==(1)
        c(0).network_campaign_id must be_==("y1")
    }}
  }

  "select(User)" should {
    sequential
    "select all campaigns of User" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val caps = Campaign.select(user)
        caps(0).user_id must_==(user.id)
        caps.length must be_==(3)

    }}
  }

  "select(User, Network)" should {
    sequential

    "select all campaigns for given User and Network" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val network = Network.select("Yandex").head
        val caps = Campaign.select(user = user, network = network)
        for(c <- caps) c.network_campaign_id must startWith("y")
        caps.length must be_==(2)
    }}

  }



  "select_latest_curve(Date)" should {
    sequential

    "select the latest Curve after Date" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val network = Network.select("Yandex").head
        val campaign = Campaign.select(user, network).head

        val formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")
        val date: DateTime = formatter.parseDateTime("2012-09-19 10:00:00")
        val curve = campaign.select_latest_curve(date.toDate).head
        ( curve.start_date.before( date.toDate ) )

        // check that we have 2 curves
        import org.squeryl.PrimitiveTypeMode.inTransaction
        inTransaction {
          campaign.curves.toList.length must be_==(2)
        }

        val curve1 = campaign.select_latest_curve(date.plusDays(1).toDate).head
        curve1.id must_!=(curve.id)

    }}
  }


  "insert(TimeSlot)" should {
    sequential

    "insert new TimeSlot w/ Mock" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val network = Network.select("Yandex").head
        val campaign = Campaign.select(user, network).head

        // make dates
        val formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")
        val start_date: DateTime = formatter.parseDateTime("2012-09-19 10:00:00")
        val end_date = start_date.plusMinutes(30)


        // inject mock TimeSlotType.get_timeslottypeo
        class MockFactory extends AppSchemaFactory {
          override def get_TimeSlotType() = {
            class MockTimeSlotType extends TimeSlotType{
              override def get_timeslottype(s: Date, e: Date): TimeSlotType = {
                val o = new TimeSlotType("foo")
                o.id = 1
                o
              }
            }
            new MockTimeSlotType
          }
        }
        campaign.app_schema_factory = new MockFactory

        // usual work flow
        val ts = TimeSlot(0, 0, start_date.toDate, end_date.toDate, 4.0, 5.0, 5,5,1,1)
        var res_ts = campaign.insert(ts)
        res_ts.id must_!=(0)
        res_ts.start_date must_==(start_date.toDate)
    }}

    "insert new TimeSlot" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val network = Network.select("Yandex").head
        val campaign = Campaign.select(user, network).head

        // make dates
        val formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")
        val start_date: DateTime = formatter.parseDateTime("2012-09-19 10:00:00")
        val end_date = start_date.plusMinutes(30)

        // usual work flow
        val ts = TimeSlot(0, 0, start_date.toDate, end_date.toDate, 4.0, 5.0, 5,5,1,1)
        var res_ts = campaign.insert(ts)
        res_ts.id must_!=(0)
        res_ts.start_date must_==(start_date.toDate)
    }}
  }


  "process_report" should {
    sequential

    "throwgh an Exception if start_date or end_date is missed" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        // get test data
        val file_name = "test/domain/serializing/helpers/report1.xml"
        val node = scala.xml.XML.loadFile(file_name)
        // create a copy w/o end_date
        val stat = <report> {(node\"stat")}</report>

        // just checking - not Exceptions
        campaign.process_report(node, serializing.helpers.YandexXmlReportHelper) must_==(true)
        // but w/ no dates
        campaign.process_report(stat, YandexXmlReportHelper) must throwA[RuntimeException] //Exception]
    }}

    def change_in_node(node: NodeSeq, pattern: String, new_content: String): NodeSeq = {
      val buf = node.toString
      val res = buf.replaceFirst(pattern, new_content)
      scala.xml.XML.loadString(res)
    }

    "throwgh an Exception if sum_search is empty" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        // get test data
        val file_name = "test/domain/serializing/helpers/report1.xml"
        val node = scala.xml.XML.loadFile(file_name)
        // create a copy w/o sum_search
        val pattern = "sum_search=\".*\""
        val bad_node = change_in_node(node, pattern, "")

        // just checking - no Exceptions
        campaign.process_report(node, YandexXmlReportHelper) must_==(true)
        // but w/ no dates
        campaign.process_report(bad_node, YandexXmlReportHelper) must throwA[RuntimeException] //Exception]
    }}

    "puts new Banner, Phrase, Region, BannerPhrase and BannerPhraseStats in DB" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        // get test data
        val file_name = "test/domain/serializing/helpers/report1.xml"
        val node = scala.xml.XML.loadFile(file_name)

        // just checking - no Exceptions
        campaign.process_report(node, YandexXmlReportHelper) must_==(true)
        // check out new entities
        // from the row 1
        val banner = Banner.select(network_banner_id = "123456").head
        banner.id must_!=(0)
        banner.network_banner_id must_==("123456")

        // from the row 2
        val region = Region.select(network_region_id = "2").head
        region.id must_!=(0)
        region.network_region_id must_==("2")

        // from the row 3
        val phrase = Phrase.select(network_phrase_id = "538205158").head
        phrase.id must_!=(0)
        phrase.network_phrase_id must_==("538205158")

        // BannerPhrase from the row 1
        val phrase1 = BannerPhrase.select(campaign, network_banner_id = "123456",
          network_phrase_id = "538205157", network_region_id = "1").head
        phrase1.id must_!=(0)
        phrase1.banner_id must_==(banner.id)

        // BannerPhrase from the row 2
        val phrase2 = BannerPhrase.select(campaign, network_banner_id = "223456",
          network_phrase_id = "538205158", network_region_id = "2").head
        phrase2.id must_!=(0)
        phrase2.region_id must_==(region.id)

        // BannerPhrase from the row 3
        val phrase3 = BannerPhrase.select(campaign, network_banner_id = "323456",
          network_phrase_id = "538205158", network_region_id = "3").head
        phrase3.id must_!=(0)
        phrase3.phrase_id must_==(phrase.id)

        // BannerPhraseStats
        import org.squeryl.PrimitiveTypeMode.inTransaction
        import org.squeryl.PrimitiveTypeMode._
        inTransaction{
          val bps = from(AppSchema.bannerphrasestats)((b) =>
            where(b.bannerphrase_id === phrase3.id
            )
            select(b)).toList

          bps.length must_==(1)
        }
    }}
  }

  "recommendations_changed_since" should {
    sequential

    "check if recommendation has been changed" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        val start_date = common.Helpers.app_formatter.parseDateTime("2012-09-20 00:00:01")
        (new Recommendation(campaign.id, start_date.toDate)).put
        // move time back
        val before = start_date.minusMinutes(1)
        campaign.recommendations_changed_since(before.toDate) must_==(true)
        // move time forward
        val after = start_date.plusMinutes(1)
        campaign.recommendations_changed_since(after.toDate) must_==(false)

    }}

    "return false if there is no recommendations at all" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        val start_date = common.Helpers.app_formatter.parseDateTime("2012-09-20 00:00:01")
        // move time back
        val before = start_date.minusMinutes(1)
        campaign.recommendations_changed_since(before.toDate) must_==(false)

    }}
  }


  "select_BannerPhrases_Permutations" should {
    sequential
    "select BannerPhrase-Permutation selection right TimeSlot" in {
      TestDB_0.running_FakeApplication() {
        import org.squeryl.PrimitiveTypeMode.inTransaction
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        val midnnight_formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd")
        val date = midnnight_formatter.parseDateTime("2012-09-19")

        inTransaction{
          val bpp = campaign.select_BannerPhrases_Permutations(date.plusMinutes(35).toDate)
          bpp.toList.length must_==(2)

          val bpp1 = campaign.select_BannerPhrases_Permutations(date.minusMinutes(30).toDate)
          bpp1.toList.length must_==(0)
        }
    }}
  }


  "select_current_recommedations" should {
    sequential
    "select Permutations and BannerPhrases (creating BannerPhraseHelper)  for the  right TimeSlot" in {
      TestDB_0.running_FakeApplication() {
import org.squeryl.PrimitiveTypeMode.inTransaction
        val campaign = Campaign.select("Coda", "Yandex", "y1").head
        val midnnight_formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd")
        val date = midnnight_formatter.parseDateTime("2012-09-19")

        inTransaction{
          // there are Permutations for this date
          val bpp = campaign.select_current_recommedations(date.plusMinutes(35).toDate)
          bpp.toList.length must_==(2)
          // check that BannerPhraseHelpers are correct
          bpp.head.phrase_id must_==("1")
          bpp.head.regionID must_==("7")

          // No Permutations for this date
          val bpp1 = campaign.select_current_recommedations(date.minusMinutes(10).toDate)
          bpp1.toList.length must_==(0)
        }
    }}
  }

}



/*
    "get banners" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val banners = camp.get_banners
        banners(0).campaign_id must be_==(1)
        banners(1).id must be_==(2)
    }}

    "get bannerphrases" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val banner_phrases = camp.get_bannerphrases
        banner_phrases(0).banner_id must be_==(1)
        banner_phrases(0).phrase_id must be_==(1)
    }}


    "get curves" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        val start_date = dateFormat.parse("2012-09-18")
        val end_date = dateFormat.parse("2012-09-21")
        val curves = camp.get_curves(start_date, end_date)
        curves(0).a must_==(3)
        curves(0).start_date.toString must_==("2012-09-19")
        curves(1).a must_==(1.1)
        curves(1).start_date.toString must_==("2012-09-20")
        curves.length must_==(2)
    }}

    "get curves when start_date > end_date" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        val start_date = dateFormat.parse("2010-09-18")
        val end_date = dateFormat.parse("2011-09-11")
        val curves = camp.get_curves(start_date, end_date)
        (curves.isEmpty)
    }}

    """get curves_timeslots == List(Curve, TimeSlot)
      ordered by Curve.start_date and TimeSlot.start_date""" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 2
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        val (start_date, end_date) = (dateFormat.parse("2012-09-18"), dateFormat.parse("2012-09-20"))
        val res = camp.get_curves_timeslots(start_date, end_date)
        ( ! res.isEmpty )
        res.length must_==( 6 )
        res(0)._1.start_date.toString must_==("2012-09-18")
        //check that it's ordered by TimeSlot.start_date
        val expect = (new SimpleDateFormat("yyyy-MM-dd")).parse("2012-09-20 10:00:00")
        res(3)._2.start_date must_==(expect)
    }}

    """get curves_timeslots_permutations == List(Curve, TimeSlot,Permutation)
      ordered by Curve.start_date and TimeSlot.start_date""" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        val (start_date, end_date) = (dateFormat.parse("2012-09-18"), dateFormat.parse("2012-09-20"))
        val res = camp.get_curves_timeslots_permutations(start_date, end_date)
        ( ! res.isEmpty )
        // check that there are only 2 Permutations
        res.length must_==( 2 )
        // check that it's ordered by Curve.start_date
        res(0)._1.start_date.toString must_==("2012-09-19")
        //check that it's ordered by TimeSlot.start_date
        val expect = (new SimpleDateFormat("yyyy-MM-dd")).parse("2012-09-19 10:30:00")
        res(1)._2.start_date must_==(expect)
    }}

    "put CampaignStats throw Exception if there is no Curve w/ start date earlier than given date" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val start_date = helpers.get_Date_from_standart_string("2010-09-19 00:00:00")
        val end_date = helpers.get_Date_from_standart_string("2010-09-19 00:30:00")
        val camp_stats = CampaignStats(0,2.0,3.0,100,50,2,1)
        camp.put_campaignstats(camp_stats, start_date, end_date) must throwA[Exception]
    }}
  }
*/


