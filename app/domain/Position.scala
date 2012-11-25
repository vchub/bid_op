package domain

import common._

/**
 * Position of BannerPhrase in the Permutation
 * bid(Curve) has to calculate a bid: Double
 */

trait Position {
  def position: Int
  //def bannerPhrase: BannerPhrase

  //TODO
  def bid(curve: Curve, bannerPhrase: BannerPhrase) =
    if (bannerPhrase.netAdvisedBidsHistory == Nil) 1
    else {
      val netAdvisedBids = bannerPhrase.netAdvisedBidsHistory.head
      val mean_advised_bid = netAdvisedBids.a + netAdvisedBids.b + netAdvisedBids.c + netAdvisedBids.d
      1 / (1 + math.exp(-curve.a * position - curve.b)) * mean_advised_bid
    }
}
