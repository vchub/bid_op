package domain

import org.joda.time.DateTime
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}


trait BannerPhrase{
  def id: Long
  def campaign: Option[Campaign]
  def banner: Option[Banner]
  def phrase: Option[Phrase]
  def region: Option[Region]
  def actualBidHistory: List[ActualBidHistoryElem]
  lazy val actualBidHistoryJList: JList[ActualBidHistoryElem] = actualBidHistory
  def recommendationHistory: List[RecommendationHistoryElem]
  lazy val recommendationHistoryJList: JList[RecommendationHistoryElem] = recommendationHistory
  def netAdvisedBidsHistory: List[NetAdvisedBids]
  lazy val netAdvisedBidsHistoryJList: JList[NetAdvisedBids] = netAdvisedBidsHistory
  def performanceHistory: List[Performance]
  lazy val performanceHistoryJList: JList[Performance] = performanceHistory

  /** Override equality
  * well it's kind of tough. on the one hand BP are equal when Banner, Phrase and Region are equal.
  * on the other I am not sure we always have those 3 object created and associated w/ BP at the
  * time of using BP as a key in a Map
  */
  override def hashCode = (41 * id).toInt
  override def equals(other: Any) = other match {
    case that: BannerPhrase =>
      // use id (primary key) only
      (that canEqual this) && (this.id == that.id)
    case _ =>
      false
  }
  def canEqual(other: Any) = other.isInstanceOf[BannerPhrase]
  // end of Equality override

}

