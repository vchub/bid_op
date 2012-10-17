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
        val campaigns = dao.getShallowCampaigns("Coda", "Yandex")
        campaigns.length must be_==(2)
    }}
  }

  "getCampaigns(userName, networkName, networkCampaignId)" should {
    sequential
    "get 1 Campaign from TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaigns = dao.getShallowCampaigns("Coda", "Yandex", "y1")
        campaigns.length must be_==(1)
        campaigns(0).network_campaign_id must be_==("y1")
    }}
  }

  def createPerformance(date: DateTime, periodType: domain.PeriodType): domain.Performance = domain.Performance(
    id = 0,
    cost_search = 1,
    cost_context = 1,
    impress_search = 1,
    impress_context = 1,
    clicks_search = 1,
    clicks_context = 1,
    periodType = periodType,
    date = date
  )

  "createCampaignPerformanceReport" should {
    sequential
    "create 1 CampaignPerformance in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val campaigns = dao.getShallowCampaigns("Coda", "Yandex", "y1")
        val periodType = domain.PeriodType(id = 1, factor = 1)
        val performance = createPerformance(campaigns(0).startDate, periodType)
        val perf_res = dao.createCampaignPerformanceReport(campaigns(0), performance)
        perf_res.id must_!=(0)
    }}
  }

  "getCampaignHistory" should {
    sequential
    "retrieve CampaignHistory from DB" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getShallowCampaigns("Coda", "Yandex", "y1")(0)

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
        val c = dao.getShallowCampaigns("Coda", "Yandex", "y1")(0)

        val campaign = dao.getCampaignHistory(c.id, c.startDate, c.endDate.getOrElse(new DateTime)).
          campaign
        val bp = campaign.bannerPhrases
        val periodType = domain.PeriodType(id = 1, factor = 1)
        // I hope there's no reports on these dates
        val (startDate, endDate) = (campaign.startDate.plusDays(5), campaign.startDate.plusDays(6))
        // it should be 4 BannerPhrases
        campaign.bannerPhrases.length must_==(4)
        // List[(BannerPhrases, Performance)]
        val report_l = bp map ((_, createPerformance(startDate, periodType)))
        // Map
        val report = report_l.toMap
        // create reports
        dao.createBannerPhrasesPerformanceReport(report)

        // check if saved
        val c_res = dao.getCampaignHistory(c.id, startDate, endDate).campaign
        val report_res = c_res.bannerPhrases map(_.performanceHistory)
        // should be 4 List[Performance]
        report_res.length must_==(4)
        // should be 1 Performance in every List
        report_res.flatten.length must_==(4)

    }}
  }

  "createPermutation" should {
    sequential
    "create 1 Permutation and 4 Positions in TestDB_0" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val c = dao.getShallowCampaigns("Coda", "Yandex", "y1")(0)

        // get Campaign
        val campaign = dao.getCampaignHistory(c.id, c.startDate, c.endDate.getOrElse(new DateTime)).
          campaign
        // get BannerPhrases
        val bp = campaign.bannerPhrases
        // fix date
        val date = c.startDate.plusDays(5)
        // create Permutation
        val gen = (0 to 3).toIterable
        val bp_p_list = for(b <- bp; i <- 0 to bp.size) yield (b, domain.Position(i))
        val bp_p_map = bp_p_list.toMap
        val permutation = domain.Permutation(date = date, permutation = bp_p_map)

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
        val fmt = format.ISODateTimeFormat.date()
        val startDate: DateTime = fmt.parseDateTime("2012-09-19")
        val endDate = startDate.plusDays(30)
        val cc = domain.Campaign(
          id = 0,
          network_campaign_id = "",
          startDate = startDate, endDate = Some(endDate),
          budget = Some(100.0),
          user = None,
          network = None,
          bannerPhrases = Nil,

          curves = List(),
          performanceHistory = List(),
          permutationHistory = List(),

          budgetHistory = Nil,
          endDateHistory = List()
        )
        val dao = new SquerylDao
        val c = dao.create(cc)
        // checking id (db primary key) is created
        c.id must_!=(0)
        // the c by names
        val c_res = dao.getShallowCampaigns("Coda", "Yandex", "y1")
        // check that we have some
        c_res.length must_==(1)
    }}
  }

}


