package domain

import scala.reflect._


@BeanInfo
case class BannerPhrase(
  val id: Long,
  val banner: Option[Banner],
  val phrase: Option[Phrase],
  val region: Option[Region],
  val actualBidHistory: List[TSValue[Double]],
  val recommendationHistory: List[TSValue[Double]],
  val netAdvisedBidsHistory: List[TSValue[NetAdvisedBids]],
  val performanceHistory: List[Performance]
) {}

