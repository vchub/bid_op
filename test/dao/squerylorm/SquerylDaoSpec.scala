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




}


