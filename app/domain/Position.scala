package domain

import common._

/** Position of BannerPhrase in the Permutation
* bid(Curve) has to calculate a bid: Double
*/

trait Position {
  def position: Int
  //def bannerPhrase: BannerPhrase

  //TODO
  def bid(curve: Curve, deviation: Double) = deviation
}
