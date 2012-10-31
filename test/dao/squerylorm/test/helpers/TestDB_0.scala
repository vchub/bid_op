package dao.squerylorm.test.helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import play.api.test._
import play.api.test.Helpers._
import org.joda.time
import java.sql.Timestamp
import scala.reflect.BeanInfo

import dao.squerylorm._


object TestDB_0 extends AppHelpers {



  val midnnight_formatter = time.format.DateTimeFormat.forPattern("yyyy-MM-dd")

  def fill_DB() = {
    //set start_date
    val date = midnnight_formatter.parseDateTime("2012-09-19")
    // partially applied start_date
    val plusDays = date.plusDays _
    //def get_from_standart_string_DateTime(date: DateTime): String = date.toString(app_formatter)

    //Users
    val users = List(User("Coda").put, User("Some").put)

    //Networks
    val networks = List(Network("Yandex").put, Network("Google").put)

    // add Campaigns to User Network
    val campaigns = List(
      users(0).networksRel.associate(networks(0), Campaign(0,0, "y1", date)),
      users(0).networksRel.associate(networks(0), Campaign(0,0, "y2", plusDays(1))),
      users(0).networksRel.associate(networks(1), Campaign(0,0, "g2", date)),
      users(1).networksRel.associate(networks(1), Campaign(0,0, "g1", date))
    )

    // add Banners to Campaigns(0)
    val banners = List(
        Banner("y_banner_1").put,
        Banner("y_banner_2").put
    )
    //Phrases
    val phrases = List(Phrase("1", "Hi").put, Phrase("2", "Bon Jour").put)

    //Regions
    val regions = List((Region(7, 0, "7", "Russia")).put, (Region(0, 7, "77", "Moscow")).put)

    //BannerPhrase
    val bannerPhrases = List(
      BannerPhrase(campaigns(0).id, banner_id = banners(0).id, phrase_id = phrases(0).id, region_id = regions(0).id),
      BannerPhrase(campaigns(0).id, banner_id = banners(0).id, phrase_id = phrases(1).id, region_id = regions(0).id),
      BannerPhrase(campaigns(0).id, banner_id = banners(0).id, phrase_id = phrases(0).id, region_id = regions(1).id),
      BannerPhrase(campaigns(0).id, banner_id = banners(0).id, phrase_id = phrases(1).id, region_id = regions(1).id)
    ).map(_.put)

    //Curves
    val curves = List(
      campaigns(0).curvesRel.associate(Curve(campaign_id = 0, date = date, a=1,b=1,c=1,d=1)),
      campaigns(0).curvesRel.associate(Curve(campaign_id = 0, date = plusDays(1), a=2,b=2,c=2,d=2)),
      campaigns(1).curvesRel.associate(Curve(campaign_id = 0, date = plusDays(2), a=0,b=0,c=0,d=0))
    )

    //PeriodType
    val periodTypes = List(PeriodType(factor = 1, description = "day").put, PeriodType(factor = 0.5, description = "night").put)

    //BudgetHistory
    val budgetHistory = List(
      campaigns(0).budgetHistoryRel.associate(BudgetHistory(campaign_id = 0, date = date, budget = 100)),
      campaigns(0).budgetHistoryRel.associate(BudgetHistory(campaign_id = 0, date = date.plusDays(10), budget = 50))
    )

    //CampaignPerformance
    val campaignPerformances = List(
      CampaignPerformance(
        campaign_id = campaigns(0).id,
        periodtype_id = periodTypes(0).id,
        date = date,
        cost_search = 1, cost_context = 1, impress_search = 10, impress_context = 10,
        clicks_search = 1, clicks_context = 1
      ).put,
      CampaignPerformance(
        campaign_id = campaigns(0).id,
        periodtype_id = periodTypes(0).id,
        date = date.plusMinutes(30),
        cost_search = 1, cost_context = 1, impress_search = 10, impress_context = 10,
        clicks_search = 1, clicks_context = 1
      ).put
    )

    //Permutations
    val permutations = List(
      Permutation(
        campaign_id = campaigns(0).id,
        date = date
      ).put
    )

    //Position
    val positions = List(
      Position(
        bannerphrase_id = bannerPhrases(0).id,
        permutation_id = permutations(0).id,
        position = 0
      ).put,
      Position(
        bannerphrase_id = bannerPhrases(1).id,
        permutation_id = permutations(0).id,
        position = 1
      ).put,
      Position(
        bannerphrase_id = bannerPhrases(2).id,
        permutation_id = permutations(0).id,
        position = 2
      ).put,
      Position(
        bannerphrase_id = bannerPhrases(3).id,
        permutation_id = permutations(0).id,
        position = 3
      ).put
    )

  }


}
