package dao.squerylorm

import org.squeryl.{ Schema, KeyedEntity, Query }
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.joda.time._
import scala.reflect._
import common._
import domain.{ Campaign => dCam, Performance => dPerf, Permutation => dPerm }

@BeanInfo
class SquerylDao extends dao.Dao {

  override def getCampaign(userName: String, networkName: String, networkCampaignId: String,
    historyStartDate: DateTime = new DateTime, historyEndDate: DateTime = new DateTime) =
    Campaign.select(userName: String, networkName: String, networkCampaignId: String).headOption map {
      campaign =>
        {
          campaign.historyStartDate = historyStartDate
          campaign.historyEndDate = historyEndDate
          campaign
        }
    }

  override def getCampaigns(userName: String, networkName: String) =
    Campaign.select(userName: String, networkName: String)

  /**
   * creates CampaignPerformance in DB
   * TODO: Optimize. It has 2 DB trips now
   */
  def createCampaignPerformanceReport(campaign: dCam, performance: dPerf) =
    CampaignPerformance(campaign, performance).put

  /**
   * creates BannerPhrasePerformance records in DB
   * TODO: Optimize of course
   */
  def createBannerPhrasesPerformanceReport(campaign: domain.Campaign, report: Map[domain.BannerPhrase, dPerf]) =
    //BannerPhrasePerformance.create(report)
    Campaign.get_by_id(campaign.id).createBannerPhrasesPerformanceReport(report)

  /**
   * creates BannerPhrase NetAdvisedBids and ActualBidHistory records in DB
   * creates new BannerPhrase in case it's not present in DB.
   * TODO: Optimize of course
   * @throw java.util.RunTimeException
   */
  def createBannerPhraseNetAndActualBidReport(campaign: domain.Campaign,
    report: Map[domain.BannerPhrase, (domain.ActualBidHistoryElem, domain.NetAdvisedBids)]): Boolean =
    Campaign.get_by_id(campaign.id).createActualBidAndNetAdvisedBids(report)

  /**
   * retrieves full domain model (Campaign and its Histories) for given Dates from DB
   * TODO: Optimize. CampaignHistory is not complete. It's done partially only
   * def getCampaignWithHistory(campaign_id: Long, historyStartDate: DateTime, historyEndDate: DateTime): domain.Campaign =
   * Campaign.selectCampaignWithHistory(campaign_id, historyStartDate, historyEndDate)
   */

  /**
   * creates Campaign record
   */
  def create(campaign: dCam) = Campaign.create(cc = campaign)

  /**
   * creates new records in EndDateHistory or BudgetHistory
   * TODO: change definition. funct. should take {new_budget: date, new_endDate: date} ...
   */
  def update(campaign: dCam, date: DateTime) = ???

  /**
   * creates new User record
   */
  def create(user: domain.User): domain.User = User.create(user)

  /**
   * updates user.name
   */
  def update(user: domain.User): domain.User = ???
  /**
   * select User by name
   */
  def getUser(name: String): Option[domain.User] = User.select(name)
  def getUser(name: String, password: String): Option[domain.User] = User.select(name, password)

  /**
   * creates new Network record
   */
  def create(network: domain.Network): domain.Network = ???
  /**
   * select Network by name
   */
  def getNetwork(name: String): Option[Network] = Network.select(name)

  /**
   * creates Permutation records
   * @throw java.util.RunTimeException
   * probably add back curve to Permutation and don't use curve in def.
   */
  def create(permutation: domain.Permutation, campaign: domain.Campaign) =
    Permutation.create(permutation, campaign)

  /**
   * Creates Permutation records
   * Calculates absolute values of bids for every position and saves it
   * as Recommendations with the same DateTime
   * Creates currentRecommendationsDate record
   *
   * @throw java.util.RunTimeException
   * probably add back curve to Permutation and don't use curve in def.
   * TODO: Fix bid calculation: it sets bid = 1 now.
   */
  def createPermutaionRecommendation(permutation: domain.Permutation, campaign: domain.Campaign,
    curve: domain.Curve): DateTime = {
    val permDateTime = permutation.dateTime
    //create domain.Recommendation
    val bpBid = permutation.permutation map {
      case (bp, pos) =>
        bp -> pos.bid(curve = curve, bannerPhrase = bp)
    }

    val recommendation = new domain.Recommendation {
      val id = 0L
      val dateTime = permDateTime
      val bannerPhraseBid = bpBid
    }
    // save it to DB
    this.create(recommendation)
    // save Permutation to DB
    this.create(permutation, campaign)
    // create RecommendationChangeDate
    RecommendationChangeDate(campaign_id = campaign.id, date = permDateTime).put
    // return dateTime
    permDateTime
  }

  /**
   * creates Recommendation record
   */
  def create(recommendation: domain.Recommendation) = RecommendationHistory.create(recommendation)

  /**
   * creates Curve record
   */
  def create(curve: domain.Curve, campaign: domain.Campaign): domain.Curve =
    Curve.create(curve: domain.Curve, campaign: domain.Campaign)

  /**
   * get PeriodType for a given DateTime
   * TODO: Fix. It's dummy now
   */
  def getPeriodType(dateTime: DateTime): domain.PeriodType = PeriodType(id = 1, factor = 1, description = "")

  /** check if Recommendation has changed since dateTime */
  def recommendationChangedSince(c: domain.Campaign, dateTime: DateTime): Boolean =
    RecommendationChangeDate.recommendationChangedSince(c.id, dateTime)

  /** get current (the earliest before dateTime) Recommendations */
  def getCurrentRecommedation(c: domain.Campaign, dateTime: DateTime = new DateTime): Option[domain.Recommendation] =
    Campaign.get_by_id(c.id).selectCurrentRecommendation(dateTime)

  def clearDB: Boolean = {
    import scala.util.control.Exception._
    inTransaction {
      try {
        allCatch opt AppSchema.drop
        allCatch opt AppSchema.create

        //Networks
        val networks = List("Yandex", "Google", "Begun")
        val fillNets = networks map (Network(_).put)
        val fillPeriodTypes = PeriodType(new DateTime()).put
        true
      } catch {
        case e => false
      }
    }
  }
}

/**
 * /** ********************************************************************************************
   * creates Banners
   */
  def create(banner: domain.Banner) = Banner.apply(banner).put
  
  /**
   * creates Phrases
   */
  def create(phrase: domain.Phrase) = Phrase.apply(phrase).put
  
  /**
   * creates Region
   */
  def create(region: domain.Region) = Region.apply(region).put

  /**
   * creates BannerPhrases
   */
  def create(c: domain.Campaign, b: domain.Banner, p: domain.Phrase, r: domain.Region) = BannerPhrase.apply(c,b,p,r).put
  /***********************************************************************************************/  
  
 * **/
