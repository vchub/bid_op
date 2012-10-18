package domain.pojo

import scala.reflect._

import domain._

@BeanInfo
case class BannerPhrase(
  val id: Long = 0,
  val banner: Option[domain.Banner],
  val phrase: Option[domain.Phrase],
  val region: Option[domain.Region],
  val actualBidHistory: domain.ActualBidHistory = Nil,
  val recommendationHistory: domain.RecommendationHistory = Nil,
  val netAdvisedBidsHistory: domain.NetAdvisedBidsHistory = Nil,
  val performanceHistory: domain.PerformanceHistory = Nil
) extends domain.BannerPhrase
{
}

