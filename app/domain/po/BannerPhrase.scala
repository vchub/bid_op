package domain.po

import scala.reflect._
import org.joda.time.DateTime

import domain._

@BeanInfo
case class BannerPhrase(
  val id: Long = 0,
  var campaign: Option[domain.Campaign] = None,
  val banner: Option[domain.Banner],
  val phrase: Option[domain.Phrase],
  val region: Option[domain.Region],
  val actualBidHistory: List[ActualBidHistoryElem] = Nil,
  val recommendationHistory: List[RecommendationHistoryElem] = Nil,
  val netAdvisedBidsHistory: List[NetAdvisedBids] = Nil,
  val performanceHistory: List[Performance] = Nil

) extends domain.BannerPhrase
{
}

