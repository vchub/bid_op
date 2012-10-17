package domain



trait BannerPhrase{
  def id: Long
  def banner: Option[Banner]
  def phrase: Option[Phrase]
  def region: Option[Region]
  def actualBidHistory: ActualBidHistory // List[TSValue[Double]]
  def recommendationHistory: RecommendationHistory
  def netAdvisedBidsHistory: NetAdvisedBidsHistory // List[TSValue[NetAdvisedBids]]
  def performanceHistory: PerformanceHistory

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

