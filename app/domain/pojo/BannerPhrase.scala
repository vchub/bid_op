package domain.pojo

import scala.reflect._

import domain._

@BeanInfo
case class BannerPhrase(
  val id: Long,
  val banner: Option[domain.Banner],
  val phrase: Option[domain.Phrase],
  val region: Option[domain.Region],
  val actualBidHistory: domain.ActualBidHistory, // List[TSValue[Double]],
  val recommendationHistory: domain.RecommendationHistory,
  val netAdvisedBidsHistory: domain.NetAdvisedBidsHistory,
  val performanceHistory: domain.PerformanceHistory
) extends domain.BannerPhrase
{
}

