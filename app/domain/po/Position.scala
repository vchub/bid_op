package domain.po

import scala.reflect._

import common._

/** Position of BannerPhrase in the Permutation
* bid(Curve) has to calculate a bid: Double
*/

@BeanInfo
case class Position(
  val position: Int
  //val bannerPhrase: BannerPhrase
) extends domain.Position
{
}
