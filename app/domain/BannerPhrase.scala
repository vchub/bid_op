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
)
{
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

