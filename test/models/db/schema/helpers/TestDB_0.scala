package models.db.schema
package helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLInnoDBAdapter}
import org.squeryl.{Session, SessionFactory}

import play.api.test._
import play.api.test.Helpers._
import play.api.Play

import com.mysql.jdbc.Driver
import org.h2.Driver

import com.codahale.jerkson.Json

import scala.io.Source

import java.util.Date
import org.joda.time._


object TestDB_0 extends AppHelpers {



  val midnnight_formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd")

  def fill_DB() = {
    //set start_date
    val date: DateTime = midnnight_formatter.parseDateTime("2012-09-19")
    // partially applied start_date
    val plusDays = date.plusDays _
    //def get_from_standart_string_DateTime(date: DateTime): String = date.toString(app_formatter)

    //Users
    val users = List(User("Coda").put, User("Some").put)

    //Networks
    val networks = List(Network("Yandex").put, Network("Google").put)

    // add Campaigns to User Network
    val campaigns = List(
      users(0).networks.associate(networks(0), Campaign( "y1", date.toDate, plusDays(10).toDate, 100)),
      users(0).networks.associate(networks(0), Campaign( "y2", plusDays(1).toDate, plusDays(10).toDate, 100)),
      users(0).networks.associate(networks(1), Campaign( "g2", date.toDate, plusDays(10).toDate, 100)),
      users(1).networks.associate(networks(1), Campaign( "g1", date.toDate, plusDays(10).toDate, 100))
    )

    // add Banners to Campaigns(0)
    val banners = List(
        campaigns(0).banners.associate(Banner(0, "y_banner_1")),
        campaigns(0).banners.associate(Banner(0, "y_banner_2"))
    )
    //Phrases
    val phrases = List(Phrase("1", "Hi").put, Phrase("2", "Bon Jour").put)

    //Regions
    val regions = List((Region(7, 0, "7", "Russia")).put, (Region(0, 7, "77", "Moscow")).put)

    //BannerPhrase
    val bannerPhrases = List(
      BannerPhrase(banner_id = banners(0).id, phrase_id = phrases(0).id, region_id = regions(0).id),
      BannerPhrase(banner_id = banners(0).id, phrase_id = phrases(1).id, region_id = regions(0).id),
      BannerPhrase(banner_id = banners(0).id, phrase_id = phrases(0).id, region_id = regions(1).id),
      BannerPhrase(banner_id = banners(0).id, phrase_id = phrases(1).id, region_id = regions(1).id)
    ).map(_.put)

    //Curves
    val curves = List(
      campaigns(0).curves.associate(Curve(start_date = date.toDate, a=1,b=1,c=1,d=1)),
      campaigns(0).curves.associate(Curve(start_date = plusDays(1).toDate, a=2,b=2,c=2,d=2)),
      campaigns(1).curves.associate(Curve(start_date = plusDays(2).toDate, a=0,b=0,c=0,d=0))
    )

    //TimeSlotTypes
    val timeSlotTypes = List(TimeSlotType("day").put, TimeSlotType("night").put)

    //TimeSlots
    val timeSlots = List(
      TimeSlot(timeslottype_id = timeSlotTypes(0).id,
        curve_id = curves(0).id,
        start_date = date.toDate,
        end_date = date.plusMinutes(30).toDate,
        sum_search = 1, sum_context = 1, impress_search = 10, impress_context = 10,
        clicks_search = 1, clicks_context = 1
      ).put,
      TimeSlot(timeslottype_id = timeSlotTypes(1).id,
        curve_id = curves(0).id,
        start_date = date.plusMinutes(30).toDate,
        end_date = date.plusMinutes(60).toDate,
        sum_search = 1, sum_context = 1, impress_search = 10, impress_context = 10,
        clicks_search = 1, clicks_context = 1
      ).put
    )

    //Permutations
    val permutations = List(
      Permutation(
        timeslot_id = timeSlots(1).id,
        position = 1,
        bid = 1, // recommended bid
        bannerphrase_id = bannerPhrases(0).id
      ).put,
      Permutation(
        timeslot_id = timeSlots(1).id,
        position = 2,
        bid = 2, // recommended bid
        bannerphrase_id = bannerPhrases(1).id
      ).put
    )

    // add Recommendation to Campaigns(0)
    val recommendations = List(
        campaigns(0).recommendations.associate(Recommendation(0, date.plusMinutes(1).toDate)),
        campaigns(0).recommendations.associate(Recommendation(0, date.plusMinutes(3).toDate))
    )

  }


}
