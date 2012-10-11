package domain

import scala.reflect._
import org.joda.time._

/** Main strucure for Optimizer
* It contains partially flatten map of the Campaign which multiple level aggregations are mapped
* and sometimes reodered to CampaignHistory's attribues
* All Histories startDate and endDate are in complience w/ CampaignHistory.startDate and endDate
* CampaignHistory generally is created by DAO and passed to Optimizer by Controller
*/

@BeanInfo
case class CampaignHistory(
  val campaign: Campaign,
  val startDate: DateTime,
  val endDate: DateTime,

  val curves: Seq[Curve],
  val campaignPerformanceHistory: PerformanceHistory,

  // bannerPhrases realized aggregations include PerformanceHistory, ActualBidHistory
  // and NetAdvisedBidsHistory
  val bannerPhrases: Seq[BannerPhrase],

  val permutationHistory: PermutationHistory,
  val optimalPermutationHistory: PermutationHistory,
){}


