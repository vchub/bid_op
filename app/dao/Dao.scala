package dao

import scala.collection.JavaConversions._
import java.util.{ Map => JMap, List => JList }
import org.joda.time._

import common._
import domain._

/**
 * Trait Dao
 */
trait Dao {
  /**
   * Shallow copies of Campaigns. Fast DB retrieval
   */
  def getCampaign(userName: String, networkName: String, networkCampaignId: String,
    historyStartDate: DateTime = new DateTime, historyEndDate: DateTime = new DateTime): Option[Campaign]

  /**
   * Shallow copies of Campaigns for gieve user and network names. Fast DB retrieval
   */
  def getCampaigns(userName: String, networkName: String): List[Campaign]

  /**
   * retrieves full domain model (Campaign and its Histories) for given Dates from DB
   * def getCampaignWithHistory(campaign_id: Long, historyStartDate: DateTime, historyEndDate: DateTime): Campaign
   */

  /**
   * creates CampaignPerformance in DB
   */
  //TODO: PeriodType is NOT set in Dao now. Fix.
  def createCampaignPerformanceReport(campaign: Campaign, performance: Performance): Performance

  /**
   * creates Performance for java.Campaign in DB
   * def createCampaignPerformanceReport(campaign: asjava.Campaign, performance: Performance):  Performance
   */

  /**
   * creates BannerPhrasePerformance records in DB
   * creates new BannerPhrase in case it's not present in DB.
   * @throw java.util.RunTimeException
   * TODO: add Exception checking in Controllers
   */
  def createBannerPhrasesPerformanceReport(campaign: Campaign, report: Map[BannerPhrase, Performance]): Boolean

  /**
   * createBannerPhrasesPerformanceReport with java.util.Map[BannerPhrase, Performance]
   */
  def createBannerPhrasesPerformanceReport(campaign: Campaign, report: JMap[BannerPhrase, Performance]): Boolean =
    createBannerPhrasesPerformanceReport(campaign, report.toMap)

  /**
   * creates BannerPhrase NetAdvisedBids and ActualBidHistory records in DB
   * creates new BannerPhrase in case it's not present in DB.
   * @throw java.util.RunTimeException
   */
  def createBannerPhraseNetAndActualBidReport(campaign: Campaign,
    report: Map[BannerPhrase, (ActualBidHistoryElem, NetAdvisedBids)]): Boolean

  /**
   * creates new Campaign record
   */
  def create(campaign: Campaign): Campaign

  /**
   * Update Campaign is in fact creating new records in EndDateHistory or BudgetHistory
   * TODO: change definition. funct. should take {new_budget: date, new_endDate: date} ...
   */
  def update(campaign: Campaign, date: DateTime): Campaign

  /**
   * creates new User record
   */
  def create(user: User): User

  /**
   * updates user.name
   */
  def update(user: User): User
  /**
   * select User by name
   */
  def getUser(name: String): Option[User]
  def getUser(name: String, password: String): Option[User]

  /**
   * creates new Network record
   */
  def create(network: Network): Network
  /**
   * select Network by name
   */
  def getNetwork(name: String): Option[Network]

  /**
   * creates Permutation records
   * @throw java.util.RunTimeException
   * probably add back curve to Permutation and don't use curve in def.
   */
  def create(permutation: Permutation, campaign: Campaign): Permutation

  /**
   * Creates Permutation records
   * Calculates absolute values of bids for every position and saves it
   * as Recommendations with the same DateTime
   * Creates currentRecommendationsDate record
   *
   * @throw java.util.RunTimeException
   * probably add back curve to Permutation and don't use curve in def.
   */
  def createPermutaionRecommendation(permutation: Permutation, campaign: Campaign, curve: Curve): DateTime

  /**
   * creates Recommendation records
   */
  def create(recommendation: Recommendation): Unit

  /**
   * creates Curve record
   */
  def create(curve: Curve, campaign: Campaign): Curve

  /** get PeriodType for a given DateTime */
  def getPeriodType(dateTime: DateTime): domain.PeriodType

  /** check if Recommendation has changed since dateTime */
  def recommendationChangedSince(c: Campaign, dateTime: DateTime): Boolean

  /** get current Recommendation */
  def getCurrentRecommedation(c: Campaign, dateTime: DateTime): Option[Recommendation]

}
