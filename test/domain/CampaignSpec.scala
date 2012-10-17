package domain

import org.specs2.mutable._
import org.specs2.specification._

import org.joda.time._


class CampaignSpec extends Specification with AllExpectations{


  def createBannerPhrases() = {
    val bannerPhrase = BannerPhrase(
      id = 0,
      banner = None,
      phrase = None,
      region = None,
      actualBidHistory = Nil,
      recommendationHistory = Nil,
      netAdvisedBidsHistory = Nil,
      performanceHistory = Nil
    )
    List(bannerPhrase, bannerPhrase, bannerPhrase, bannerPhrase)
  }

  def createCampaign() = {
    val fmt = format.ISODateTimeFormat.date()
    val startDate: DateTime = fmt.parseDateTime("2012-09-19")
    val endDate = startDate.plusDays(30)
    val budgetHistory = List(TSValue(startDate.plusDays(10), 50.0),TSValue(startDate, 100.0)).sorted

    Campaign(
      id = 0,
      network_campaign_id = null,
      startDate = startDate, endDate = Some(endDate),
      budget = budgetHistory.lastOption map(_.elem),
      user = None,
      network = None,
      bannerPhrases = createBannerPhrases,

      curves = List(),
      performanceHistory = List(),
      permutationHistory = List(),

      budgetHistory = budgetHistory,
      endDateHistory = List()
    )

  }


  "budget" should {
    "select the latest (current) budget" in {
      val campaign = createCampaign
      campaign.budget must_==(Some(50.0))
      campaign.budget.get must_==(50.0)
    }
  }

  "bannerPhrases" should {
    "select the bannerPhrases" in {
      val campaign = createCampaign
      campaign.bannerPhrases.length must_==(4)
      campaign.bannerPhrases(0).id must_==(0)
    }
  }

}
