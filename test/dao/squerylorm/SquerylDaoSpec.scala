package dao.squerylorm

import org.specs2.mutable._
import org.specs2.specification._

import org.joda.time._
import scala.xml._

import dao.squerylorm.test.helpers._

class SquerylDaoSpec extends Specification with AllExpectations {

  "getCampaigns(userName, networkName)" should {
    sequential
    "get 2 Campaigns from TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaigns = dao.getCampaigns("Coda", "Yandex")
        campaigns.length must be_==(2)
    }}
  }

  "getCampaign(userName, networkName, networkCampaignId)" should {
    sequential
    "get 1 Campaign from TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaign = dao.getCampaign("Coda", "Yandex", "y1")
        campaign.nonEmpty must be_==(true)
        campaign.get.network_campaign_id must be_==("y1")
    }}
  }

  def createPerformance(date: DateTime, periodType: domain.pojo.PeriodType): domain.Performance =
    domain.pojo.Performance(
      id = 0,
      cost_search = 1,
      cost_context = 1,
      impress_search = 1,
      impress_context = 1,
      clicks_search = 1,
      clicks_context = 1,
      periodType = periodType,
      dateTime = date
    )

  "createCampaignPerformanceReport" should {
    sequential
    "create 1 CampaignPerformance in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        val periodType = domain.pojo.PeriodType(id = 1, factor = 1, description = "")
        val performance = createPerformance(campaign.startDate, periodType)
        val perf_res = dao.createCampaignPerformanceReport(campaign, performance)
        perf_res.id must_!=(0)
    }}
  }

  "getCampaignHistory" should {
    sequential
    "retrieve CampaignHistory from DB" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        val c_history = dao.getCampaignHistory(c.id, c.startDate, c.endDate.getOrElse(new DateTime))
        val campaign = c_history.campaign
        campaign.budgetHistory.length must_==(2)
        campaign.bannerPhrases.length must_==(4)
        campaign.performanceHistory.length must_==(2)
        // check Curves
        campaign.curves.length must_==(2)
        // check Permutations
        // it should be 1 Permutation
        campaign.permutationHistory.length must_==(1)
        // Permutation should has 4 elems
        campaign.permutationHistory(0).permutation.size must_==(4)

    }}
  }


  "createBannerPhrasesPerformanceReport" should {
    sequential
    "create 4 BannerPhrasePerformance in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        val campaign = dao.getCampaignHistory(c.id, c.startDate, c.endDate.getOrElse(new DateTime)).
          campaign
        val bp = campaign.bannerPhrases
        val periodType = domain.pojo.PeriodType(id = 1, factor = 1, description = "")
        // I hope there's no reports on these dates
        val (startDate, endDate) = (campaign.startDate.plusDays(5), campaign.startDate.plusDays(6))
        // it should be 4 BannerPhrases
        campaign.bannerPhrases.length must_==(4)
        // List[(BannerPhrases, Performance)]
        val report_l = bp map ((_, createPerformance(startDate, periodType)))
        // Map
        val report = report_l.toMap
        // create reports
        dao.createBannerPhrasesPerformanceReport(campaign, report)
        // one more time create reports
        dao.createBannerPhrasesPerformanceReport(campaign, report)

        // check if saved
        val c_res = dao.getCampaignHistory(c.id, startDate, endDate).campaign
        val report_res = c_res.bannerPhrases map(_.performanceHistory)
        // should be 4 List[Performance]
        report_res.length must_==(4)
        // should be 1 Performance in every List
        report_res.flatten.length must_==(8)
    }}

    "create 1 new BannerPhrases and BannerPhrasePerformance in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        //check initial configuration
        // should be 4 BannerPhrases
        val bp1 = c.bannerPhrases
        bp1.length must_==(4)
        val bp1perf = bp1 map(_.performanceHistory)
        // should be 0 Performance in every List
        bp1perf.flatten.length must_==(0)

        // create bannerPhrase
        val bp = List[domain.BannerPhrase](domain.pojo.BannerPhrase(
            banner= Some(domain.pojo.Banner(network_banner_id = "bb00")),
            phrase= Some(domain.pojo.Phrase(network_phrase_id = "pp00")),
            region= Some(domain.pojo.Region(network_region_id = "rr00"))
          )
        )
        val periodType = domain.pojo.PeriodType(id = 1, factor = 1, description = "")
        // I hope there's no reports on these dates
        val (startDate, endDate) = (c.startDate.plusDays(5), c.startDate.plusDays(6))
        // List[(BannerPhrases, Performance)]
        // and then Map
        val report = (bp map ((_, createPerformance(startDate, periodType)))).toMap
        // create reports
        dao.createBannerPhrasesPerformanceReport(c, report) must_==(true)
        dao.createBannerPhrasesPerformanceReport(c, report) must_==(true)

        /*
        // check if saved
        val c_res = dao.getCampaignHistory(c.id, startDate, endDate).campaign
        // should be 5 BannerPhrases
        val bp_res = c_res.bannerPhrases
        bp_res.length must_==(5)
        val report_res = c_res.bannerPhrases map(_.performanceHistory)
        // should be 5 List[Performance]
        report_res.length must_==(5)
        // should be 2 Performances
        report_res.flatten.length must_==(2)
        */

        // check if saved (using Schema relations instead of CampaignHistory)
        // Notice: dao.getCampaignHistory() doesn't retrieve new added changes to DB
        import org.squeryl.PrimitiveTypeMode._
        inTransaction{
          val b_res = c.bannersRel.toList
          val bp_res = (b_res map (_.bannerPhrasesRel.toList)).flatten
          // should be 5 BannerPhrases
          bp_res.length must_==(5)
          // should be 2 Performances
          val report_res = bp_res map (_.performanceHistory)
          report_res.length must_==(5)
          report_res.flatten.length must_==(2)
        }


    }}
  }

  "createPermutation" should {
    sequential
    "create 1 Permutation and 4 Positions in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        // get Campaign
        val campaign = dao.getCampaignHistory(c.id, c.startDate, c.endDate.getOrElse(new DateTime)).
          campaign
        // get BannerPhrases
        val bp = campaign.bannerPhrases
        // fix date
        val date = c.startDate.plusDays(5)
        // create Permutation
        val gen = (0 to 3).toIterable
        val bp_p_list = for(b <- bp; i <- 0 to bp.size) yield (b, domain.pojo.Position(i))
        val bp_p_map = bp_p_list.toMap
        val permutation = domain.pojo.Permutation(dateTime = date, permutation = bp_p_map)

        // get curve
        val curve = campaign.curves(1)
        // create Permutation record
        dao.create(curve = curve, permutation = permutation)

        // check in DB
        val c_res = dao.getCampaignHistory(c.id, c.startDate, date.plusDays(1)).campaign
        // it should be 1 Permutation
        c_res.permutationHistory.length must_==(2)
        // Permutations should has 4 elems
        c_res.permutationHistory(1).permutation.size must_==(4)

    }}
  }

  "create(Campaign)" should {
    sequential
    "create 1 Campaign in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val fmt = format.ISODateTimeFormat.date()
        val startDate: DateTime = fmt.parseDateTime("2012-09-19")
        val endDate = startDate.plusDays(30)
        val cc = domain.pojo.Campaign(
          id = 0,
          network_campaign_id = "y100",
          startDate = startDate, endDate = Some(endDate),
          budget = Some(100.0),
          user = dao.getUser("Coda"),
          network = dao.getNetwork("Yandex"),
          bannerPhrases = Nil,

          curves = List(),
          performanceHistory = List(),
          permutationHistory = List(),

          budgetHistory = Nil,
          endDateHistory = List()
        )
        val c = dao.create(cc)
        // checking id (db primary key) is created
        c.id must_!=(0)
        // the c by names
        val c_res = dao.getCampaign("Coda", "Yandex", "y100").get
        // check that we have some
        c_res.id must_==(c.id)
    }}
  }

  "create(Recommendation)" should {
    sequential
    "create 4 Recommendations (for 4 BannerPhrases) in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        // get Campaign
        val campaign = dao.getCampaignHistory(c.id, c.startDate, c.endDate.getOrElse(new DateTime)).
          campaign
        // get BannerPhrases
        val bp = campaign.bannerPhrases
        // fix date
        val date = c.startDate.plusDays(5).plusMinutes(30)
        // create Recommendation
        val bp_list = for(b <- bp; i <- 1 to bp.size + 1) yield (b, i.toDouble)
        val bp_map = bp_list.toMap
        val recommendation = domain.pojo.Recommendation(dateTime = date, bannerPhraseBid = bp_map)

        // create Recommendation record
        dao.create(recommendation)

        // check in DB
        val c_res = dao.getCampaignHistory(c.id, c.startDate, date.plusDays(1)).campaign
        // collect recommendation from 4 BannerPhrases
        val rec_res = c_res.bannerPhrases map(_.recommendationHistory)
        rec_res.length must_==(4)

        // just checking, try to add more than 4 (from 4) BannerPhrases to Map
        // create Recommendation
        val bp_4_l = for(b <- bp++bp; i <- 0 to bp.size * 2) yield (b, i.toDouble)
        bp_4_l.toMap.size must_==(4)

    }}
  }

}


