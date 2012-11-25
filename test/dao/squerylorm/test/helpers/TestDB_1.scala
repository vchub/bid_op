package dao.squerylorm.test.helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import play.api.test._
import play.api.test.Helpers._
import org.joda.time
import java.sql.Timestamp
import scala.reflect.BeanInfo

//import scala.util.Random

import dao.squerylorm._

object TestDB_1 extends AppHelpers {

  def fill_DB() = {

    val rnd = new java.util.Random()

    val NumberOfUser = 1
    val NumberOfNetworks = 1
    val NumberOfCampaigns = 1
    val NumberOfRegions = 1

    val NumberOfBanners = 5
    val NumberOfPhrasesInBanner = 10

    val NumOfDays = 10 //how many days passed
    val NumOfTimestamps = 48 * NumOfDays + 24

    /**
     * how many timestamps passed in a last day
     * 1 timestamp = 30 min => 48 timestamps in a day
     */

    val midnnight_formatter = time.format.DateTimeFormat.forPattern("yyyy-MM-dd")
    //set start_date
    val date = midnnight_formatter.parseDateTime("2012-09-19")
    // partially applied start_date
    val plusDays = date.plusDays _
    //def get_from_standart_string_DateTime(date: DateTime): String = date.toString(app_formatter)
    val plusMinutes = date.plusMinutes _

    //---------------------------------

    //Users
    val users = 0 until NumberOfUser map (i => User("User_" + i.toString).put)

    //Networks
    val networks = 0 until NumberOfNetworks map (i => Network("Network_" + i.toString).put)

    // add Campaigns to User Network
    val campaigns = List(users(0).networksRel.associate(networks(0), Campaign(0, 0, "Net_0_Id", date)))

    //BudgetHistory
    val budgetHistory = List(
      campaigns(0).budgetHistoryRel.associate(BudgetHistory(campaign_id = 0, date = date, budget = 100)),
      campaigns(0).budgetHistoryRel.associate(BudgetHistory(campaign_id = 0, date = date.plusDays(10), budget = 50)))
    //---------------------------------

    // add Banners to Campaigns(0)
    val banners = 0 until NumberOfBanners map (i => Banner("Banner_" + i.toString).put)

    //Phrases
    val phrases = for (i <- 0 until NumberOfBanners; j <- 0 until NumberOfPhrasesInBanner)
      yield Phrase(((i * NumberOfPhrasesInBanner) + j).toString, "Phrase_" + i.toString + j.toString()).put

    //Regions
    val regions = List((Region(7, 0, "7", "Russia")).put)

    //BannerPhrase
    val bannerPhrases = for (i <- 0 until NumberOfBanners; j <- 0 until NumberOfPhrasesInBanner)
      yield BannerPhrase(campaigns(0).id,
      banner_id = banners(i).id,
      phrase_id = phrases((i * NumberOfPhrasesInBanner) + j).id,
      region_id = regions(0).id).put

    //NetAdvisedBidHistory
    val netAdvisedBidHistory = for (i <- 0 until NumOfTimestamps; bp <- bannerPhrases)
      yield NetAdvisedBidHistory(
      bp.id,
      plusMinutes(30 * i),
      a = rnd.nextInt(6) + 1,
      b = rnd.nextInt(5) + 1,
      c = rnd.nextInt(4) + 1,
      d = rnd.nextInt(3) + 1,
      e = rnd.nextInt(2) + 1,
      f = rnd.nextInt(1) + 1).put

    //ActualBidHistory
    val actualBidHistory = for (i <- 0 until NumOfTimestamps; bp <- bannerPhrases)
      yield ActualBidHistory(
      bp.id,
      plusMinutes(30 * i),
      bid = rnd.nextInt(6) + 1).put

    //RecommendationHistory
    val recommendationHistory = for (i <- 0 until NumOfTimestamps; bp <- bannerPhrases)
      yield RecommendationHistory(
      bp.id,
      plusMinutes(30 * i),
      bid = rnd.nextInt(6) + 1).put

    //--------------------------------

    //PeriodType
    val periodTypes = List(PeriodType(factor = 1, description = "day").put, PeriodType(factor = 0.5, description = "night").put)

    //CampaignPerformance
    def createCampaignPerformance(ts: Timestamp): CampaignPerformance = {
      CampaignPerformance(
        campaign_id = campaigns(0).id,
        periodtype_id = periodTypes(0).id,
        date = ts,
        cost_search = 1, cost_context = 1, impress_search = 10, impress_context = 10,
        clicks_search = 1, clicks_context = 1)
    }
    val campaignPerformances = 0 until NumOfTimestamps map
      (i => createCampaignPerformance(plusMinutes(30 * i)).put)
    //--------------------------------

    //PermutationsHistory!!!
    val permutations = 0 until NumOfTimestamps map
      (i => Permutation(campaign_id = campaigns(0).id,
        date = plusMinutes(30 * i)).put)
    //--------------------------------

    //Position
    /**val positions = for (bP_i <- bannerPhrases; p_j <- permutations)
      yield Position(
      bannerphrase_id = bP_i.id,
      permutation_id = p_j.id,
      position = rnd.nextInt(bannerPhrases.length)).put*/

    val positions1 = for (p_j <- permutations)
      yield (scala.util.Random.shuffle(0 until bannerPhrases.length) zip bannerPhrases) map {
      case (ind, bP_i) => Position(
        bannerphrase_id = bP_i.id,
        permutation_id = p_j.id,
        position = ind).put
    }
    //--------------------------------

    //Curves
    def createCurve(ts: Timestamp, i: Int): Curve = {
      Curve(campaign_id = 0, optimalPermutation_id = Some(48 * i + 1), a = rnd.nextGaussian(),
        b = rnd.nextGaussian(), c = rnd.nextGaussian(), d = rnd.nextGaussian(), date = ts)
    }
    val curves = 0 until NumOfDays map
      (i => campaigns(0).curvesRel.associate(createCurve(plusDays(i), i)))
    //--------------------------------

  }

}