package domain

import org.joda.time._


/** Permutation happens at DateTime and associated w/ Curve
*/
trait Permutation
{
  def dateTime: DateTime
  //val curve: Curve,
  def permutation: Map[BannerPhrase, Position]
}

