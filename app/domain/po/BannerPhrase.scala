package domain.po

import scala.reflect._

import domain._

@BeanInfo
case class BannerPhrase(
  val id: Long = 0,
  val banner: Option[domain.Banner],
  val phrase: Option[domain.Phrase],
  val region: Option[domain.Region],
  val actualBidHistory: List[TSValue[Double]] = Nil,
  val recommendationHistory: List[TSValue[Double]] = Nil,
  val netAdvisedBidsHistory: List[NetAdvisedBids] = Nil,
  val performanceHistory: List[Performance] = Nil

) extends domain.BannerPhrase
{
}

