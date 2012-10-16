package dao.squerylorm

import org.squeryl.{Schema, KeyedEntity, Query}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.util.Date
import org.joda.time._
import scala.reflect._
import common._
import domain.{Campaign => dCam, Performance => dPerf, Permutation => dPerm}


@BeanInfo
class SquerylDao extends dao.Dao
{



  override def getShallowCampaigns(userName: String, networkName: String, networkCampaignId: String) =
    Campaign.select(userName: String, networkName: String, networkCampaignId: String) map (Campaign.shallow_mapper(_, this))

  override def getShallowCampaigns(userName: String, networkName: String) =
    Campaign.select(userName: String, networkName: String) map (Campaign.shallow_mapper(_, this))


  /** creates CampaignPerformance in DB
  * TODO: Optimize. It has 2 DB trips now
  */
  def createCampaignPerformanceReport(campaign: dCam, performance: dPerf) =
    CampaignPerformance(campaign, performance).put.domainPerformance


  /** creates BannerPhrasePerformance records in DB
  */
  def createBannerPhrasesPerformanceReport(report: Map[domain.BannerPhrase, dPerf]) =
    BannerPhrasePerformance.create(report)


  /** retrieves full domain model (Campaign and its Histories) for given Dates from DB
  * TODO: Optimize. CampaignHistory is not complete. It's done partially only
  */
  def getCampaignHistory(campaign_id: Long, startDate: DateTime, endDate: DateTime): domain.CampaignHistory =
    Campaign.selectCampaignHistory(campaign_id: Long, startDate: DateTime, endDate: DateTime, daos = this)




  def createCampaign(campaign:dCam) = ???

  /** creates new records in EndDateHistory or BudgetHistory
  */
  def updateCampaign(campaign:dCam, date: DateTime) = ???



  def createPermutation(curve: domain.Curve, permutation: domain.Permutation): Unit =
    Permutation.create(curve, permutation)

  def createRecommendation(campaign: domain.Campaign, recommendation:domain.Recommendation) = ???




}

