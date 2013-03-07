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
      }
    }

    "Campaigns are shallow objects i.e. all aggregates are Nil" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaigns = dao.getCampaigns("Coda", "Yandex")
        val c = campaigns(0)
        c.id must_!= (0)
        c.network_campaign_id must_!= ("")
        c.startDate.isAfter(0L) must_== (true)
        c.endDate must_== (None)
        c.budget must_!= (0)

        c.user must_== (None)
        c.network must_== (None)

        c.bannerPhrases must_!= (Nil)

        // start and end Dates of retrieved Campaign Histories
        c.historyStartDate.isAfter(0) must_== (true)
        c.historyEndDate.isAfter(0) must_== (true)

        c.curves must_== (Nil)
        c.performanceHistory must_== (Nil)
        c.permutationHistory must_== (Nil)

        c.budgetHistory must_== (Nil)
        c.endDateHistory must_== (Nil)
      }
    }
  }

  "getCampaign(userName, networkName, networkCampaignId)" should {
    sequential
    "get 1 Campaign from TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaign = dao.getCampaign("Coda", "Yandex", "y1")
        campaign.nonEmpty must be_==(true)
        campaign.get.network_campaign_id must be_==("y1")
      }
    }
  }

  def createPerformance(date: DateTime, periodType: domain.po.PeriodType): domain.Performance =
    domain.po.Performance(
      id = 0,
      cost_search = 1,
      cost_context = 1,
      impress_search = 1,
      impress_context = 1,
      clicks_search = 1,
      clicks_context = 1,
      periodType = periodType,
      dateTime = date)

  "createCampaignPerformanceReport" should {
    sequential
    "create 4 CampaignPerformance in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        val periodType = new domain.po.PeriodType(id = 1, factor = 1, description = "")
        //startDate
        val date = new DateTime
        // make 4 performances (in reverse order)
        val performances = for (i <- 90 to 0 by -30) yield createPerformance(date.plusMinutes(i), periodType)
        // store them in DB
        val perf_stored = performances map (dao.createCampaignPerformanceReport(campaign, _))
        // check that stored
        perf_stored.length must_== (4)
        perf_stored.foreach(_.id must_!= (0))

        //retrieve from DB, check ascending order and in conformance to historyStartDate, historyEndDate
        val c = dao.getCampaign("Coda", "Yandex", "y1").get
        c.historyStartDate = date.plusMinutes(25)
        c.historyEndDate = date.plusMinutes(85)
        val res = c.performanceHistory
        res.length must_== (2)
        res(0).dateTime must_== (performances(2).dateTime)
        res(1).dateTime must_== (performances(1).dateTime)

      }
    }
  }

  "createBannerPhrasesPerformanceReport" should {
    sequential

    "create 4 BannerPhrasePerformances for the 4 existing BannerPhrases in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get
        // take all all history
        c.historyStartDate = new DateTime(0)
        c.historyEndDate = new DateTime
        val date = c.historyEndDate

        // it should be 4 BannerPhrases prefilled in DB
        c.bannerPhrases.length must_== (4)

        //create Mock PeriodType
        val periodType = new domain.po.PeriodType(id = 1, factor = 1, description = "")

        // Map[(BannerPhrases, Performance)]
        val report1 = (c.bannerPhrases map ((_, createPerformance(date, periodType)))).toMap
        val report2 = (c.bannerPhrases map ((_, createPerformance(date.plusMinutes(1), periodType)))).toMap

        // create report
        dao.createBannerPhrasesPerformanceReport(c, report1) must_== (true)
        // one more time create report
        dao.createBannerPhrasesPerformanceReport(c, report2) must_== (true)

        // check if saved
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        campaign.historyStartDate = date
        campaign.historyEndDate = date.plusMinutes(2)
        val bp_performanceHistories = campaign.bannerPhrases map (_.performanceHistory)
        // should be 4 PerformanceHistories
        bp_performanceHistories.length must_== (4)
        // should be 2 Performances in every PerformanceHistory
        bp_performanceHistories.flatten.length must_== (8)

        //Histories are in ascending order
        (bp_performanceHistories(0)(0).dateTime before bp_performanceHistories(0)(1).dateTime)
      }
    }

    "create 1 new BannerPhrase, 1 Banner, 1 Phrase, 1 Region and 2 BannerPhrasePerformances in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        // create bannerPhrase
        val bp = List[domain.BannerPhrase](domain.po.BannerPhrase(
          banner = Some(domain.po.Banner(network_banner_id = "bb00")),
          phrase = Some(domain.po.Phrase(network_phrase_id = "pp00")),
          region = Some(domain.po.Region(network_region_id = "rr00"))))
        val periodType = new domain.po.PeriodType(id = 1, factor = 1, description = "")
        val date = new DateTime
        // Map [BannerPhrases, Performance]
        val report1 = (bp map ((_, createPerformance(date, periodType)))).toMap
        val report2 = (bp map ((_, createPerformance(date.plusDays(1), periodType)))).toMap

        //get campaign
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get
        // create reports
        dao.createBannerPhrasesPerformanceReport(c, report1) must_== (true)
        dao.createBannerPhrasesPerformanceReport(c, report2) must_== (true)

        // check if saved
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        campaign.historyStartDate = date
        campaign.historyEndDate = date.plusDays(2)
        // find new BannerPhrase
        val bps = campaign.bannerPhrases filter (_.banner.get.network_banner_id == "bb00")
        //only BannerPhrase is created
        bps.length must_== (1)
        val created_bp = bps.head
        // check phrase is created
        created_bp.phrase.get.network_phrase_id must_== ("pp00")
        // check 2 Performances are created
        created_bp.performanceHistory.length must_== (2)
        // performanceHistory is in a hronological order
        created_bp.performanceHistory(0).dateTime must_== (date)

      }
    }
  }

  "createPermutation" should {
    sequential
    "create 1 Permutation and 4 Positions in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        val date = new DateTime
        val gen = (0 to 3).toIterable
        // create Permutation Map[BannerPhrase, Position]
        val permutation_map = (for (b <- c.bannerPhrases; i <- 0 to c.bannerPhrases.length)
          yield (b, domain.po.Position(i)))
          .toMap
        // create domain.Permutation
        val permutation2 = domain.po.Permutation(0, dateTime = date.plusMinutes(1), permutation = permutation_map)
        val permutation1 = domain.po.Permutation(0, dateTime = date, permutation = permutation_map)

        // save in DB
        dao.create(permutation2, c)
        dao.create(permutation1, c)

        // check in DB
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        campaign.historyStartDate = date
        campaign.historyEndDate = date.plusMinutes(2)

        // it should be 2 Permutation
        campaign.permutationHistory.length must_== (2)
        // Permutations should has 4 elems
        campaign.permutationHistory forall (_.permutation.size must_== (4))
        // permutationHistory is in a chronological order
        (campaign.permutationHistory(0).dateTime before campaign.permutationHistory(1).dateTime)
      }
    }
  }

  "create(Campaign)" should {
    sequential
    "create 1 Campaign in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val fmt = format.ISODateTimeFormat.date()
        val startDate: DateTime = fmt.parseDateTime("2012-09-19")
        val endDate = startDate.plusDays(30)
        val cc = new domain.po.Campaign(
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
          endDateHistory = List())
        // create Campaign in DB
        val c = dao.create(cc)
        // checking id (db primary key) is created
        c.id must_!= (0)

        // Histories have to be saved in DB
        val campaign = dao.getCampaign("Coda", "Yandex", "y100").get
        campaign.historyStartDate = startDate
        campaign.historyEndDate = endDate
        campaign.budget.get must_== (100)
        campaign.budgetHistory.length must_== (1)
        campaign.endDateHistory.length must_== (1)
      }
    }
  }

  "create Curve" should {
    sequential
    "create 2 Curves and 2 optimal Permutation in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        val date = new DateTime
        val gen = (0 to 3).toIterable
        // create Permutation Map[BannerPhrase, Position]
        val permutation_map = (for (b <- c.bannerPhrases; i <- 0 to c.bannerPhrases.length)
          yield (b, domain.po.Position(i)))
          .toMap
        // create domain.Permutation
        val permutation2 = domain.po.Permutation(0, dateTime = date.plusMinutes(1), permutation = permutation_map)
        val permutation1 = domain.po.Permutation(0, dateTime = date, permutation = permutation_map)

        // create Curves
        val curve1 = domain.po.Curve(0, 1, 1, 1, 1, date, Some(permutation1))
        val curve2 = domain.po.Curve(0, 1, 1, 1, 1, date.plusMinutes(1), Some(permutation2))

        // save Curves in DB
        dao.create(curve2, c)
        dao.create(curve1, c)

        // check in DB
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        campaign.historyStartDate = date
        campaign.historyEndDate = date.plusMinutes(2)

        // it should be 2 curves
        campaign.curves.length must_== (2)
        // it should be 2 Permutation
        campaign.permutationHistory.length must_== (2)
        // curves are in a chronological order
        (campaign.curves(0).dateTime before campaign.curves(1).dateTime)
        // curves have to have correct optimalPermutations
        campaign.curves(0).optimalPermutation.get must_== (campaign.permutationHistory(0))
      }
    }
  }

  "create(Recommendation)" should {
    sequential
    "create 4 Recommendations (for 4 BannerPhrases) in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getCampaign("Coda", "Yandex", "y1").get

        // fix date
        val date = new DateTime
        // create Recommendation
        val bp_bid_map = (for (b <- c.bannerPhrases; i <- 1 to c.bannerPhrases.size + 1)
          yield (b, i.toDouble)).toMap
        val recommendation1 = new domain.po.Recommendation(0, dateTime = date, bannerPhraseBid = bp_bid_map)
        val recommendation2 = new domain.po.Recommendation(0, dateTime = date.plusMinutes(1), bannerPhraseBid = bp_bid_map)

        // create Recommendation records
        dao.create(recommendation2)
        dao.create(recommendation1)

        // check in DB
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        campaign.historyStartDate = date
        campaign.historyEndDate = date.plusMinutes(2)

        //for every BannerPhrase there are 2 Recommendations
        campaign.bannerPhrases forall (_.recommendationHistory.length must_== (2))
        // Recommendations are in a chronological order
        (campaign.bannerPhrases(0).recommendationHistory(0).dateTime
          before campaign.bannerPhrases(0).recommendationHistory(1).dateTime)
      }
    }
  }

}


