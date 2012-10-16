package dao

import org.joda.time._
import _root_.domain._

/** Trait Dao
*/
trait Dao {
  // Shallow copies of Campaigns. Fast DB retrieval
  def getShallowCampaigns(userName: String, networkName: String, networkCampaignId: String): List[ Campaign ]
  def getShallowCampaigns(userName: String, networkName: String): List[Campaign]

  /** retrieves full domain model (Campaign and its Histories) for given Dates from DB
  */
  def getCampaignHistory(campaign_id: Long, startDate: DateTime, endDate: DateTime): CampaignHistory


  /** creates CampaignPerformance in DB
  */
  def createCampaignPerformanceReport(campaign: Campaign, performance: Performance):  Performance 

  /** creates BannerPhrasePerformance records in DB
  * @throw java.util.RunTimeException
  * TODO: add Exception checking in Controllers
  */
  def createBannerPhrasesPerformanceReport(report: Map[BannerPhrase,Performance]): Unit



  /** creates new Campaign record
  */
  def createCampaign(campaign: Campaign): Campaign

  /** creates new records in EndDateHistory or BudgetHistory
  */
  def updateCampaign(campaign: Campaign, date: DateTime): Campaign


  /** creates Permutation record
  * @throw java.util.RunTimeException
  * TODO: add Exception checking in Controllers
  */
  def createPermutation(curve: domain.Curve, permutation: domain.Permutation): Unit

  /** creates Recommendation records
  */
  def createRecommendation(campaign: domain.Campaign, recommendation: Recommendation): Boolean

}
