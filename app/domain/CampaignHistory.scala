package domain

import scala.reflect._
import org.joda.time._

/** Main strucure for Optimizer
* contains flatmap of the domain model which aggregations are mapped to CampaignHistory attribues
* CampaignHistory has startDate and endDate
* CampaignHistory generally is created by DAO and passed to Optimizer by Controller
*/

@BeanInfo
case class CampaignHistory(
  val startDate: DateTime,
  val endDate: DateTime,

  val campaign: Campaign,
  val bannerPhrases: Seq[BannerPhrase],

  val curveHistory: CurveHistory,
  val campaignPerformanceHistory: PerformanceHistory,

  val permutationHistory: PermutationHistory,
  val optimalPermutationHistory: PermutationHistory,


  //TODO: PerformanceHistory.startDate and endDate have to comply to CampaignHistory.startDate and endDate
  val bannerPhrasePerformanceHistory: Seq[(DateTime, Map[BannerPhrase, PerformanceHistory])],
  val bannerPhraseActualBidHistory: Seq[(DateTime, Map[BannerPhrase, TimeSeries])],
  val bannerPhraseRecommendedBidHistory: Seq[(DateTime, Map[BannerPhrase, TimeSeries])]
){}


