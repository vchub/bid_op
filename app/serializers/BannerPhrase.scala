package serializers

import org.joda.time.DateTime

import domain._

case class BannerPhrase(
  val banner: Option[domain.Banner],
  val phrase: Option[domain.Phrase],
  val region: Option[domain.Region]) extends domain.BannerPhrase {
  @transient
  val id: Long = 0
  @transient
  var campaign: Option[domain.Campaign] = None

  @transient
  val actualBidHistory: List[ActualBidHistoryElem] = Nil
  @transient
  val recommendationHistory: List[RecommendationHistoryElem] = Nil
  @transient
  val netAdvisedBidsHistory: List[NetAdvisedBids] = Nil
  @transient
  val performanceHistory: List[Performance] = Nil
}