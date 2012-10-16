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
  * TODO: Optimize. It has 2 DB trips now
  */
  def createCampaignPerformanceReport(campaign: Campaign, performance: Performance):  Performance 

  /** creates BannerPhrasePerformance records in DB
  * TODO: Optimize. It has 2 DB trips for every record now
  */
  def createBannerPhrasesPerformanceReport(campaign: Campaign,
    report: Map[BannerPhrase,Performance]): Boolean



  def createCampaign(campaign: Campaign): Campaign

  // creates new records in EndDateHistory or BudgetHistory
  def updateCampaign(campaign: Campaign, date: DateTime): Campaign


  def createPermutation(campaign: Campaign, permutation: Permutation): Boolean

  def createRecommendation(campaign: Campaign, recommendation: Recommendation): Boolean

}
