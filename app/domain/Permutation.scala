package domain

import scala.reflect._
import org.joda.time._


/** Permutation happens at DateTime and associated w/ Curve
*/
@BeanInfo
class Permutation(
  val date: DateTime,
  val curve: Curve,
  val pemutation: Map[BannerPhrase, Position]
) {}

