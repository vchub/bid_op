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
  def bid(curve: Curve, bannerPhrase: BannerPhrase):Double =
    if (bannerPhrase.netAdvisedBidsHistory == Nil) {println("<<<<<<<<<<<< 0.01 >>>>>>>>>>>");0.01}
    else {
      val netAdvisedBids = bannerPhrase.netAdvisedBidsHistory.head
      val mean_advised_bid = netAdvisedBids.a + netAdvisedBids.b + netAdvisedBids.c + netAdvisedBids.d
      val v = 1 / (1 + math.exp(-curve.a * position.toDouble - curve.b)) * mean_advised_bid
      println("<<<<<<<<<<<< "+v+" >>>>>>>>>>>")
      v
    }

  def bid(curve: Curve) = 1 / (1 + math.exp(-curve.a * position.toDouble - curve.b))
}
