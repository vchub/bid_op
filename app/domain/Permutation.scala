package domain

import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}


/** Permutation happens at DateTime and associated w/ Curve
*/
trait Permutation
{
  def id: Long
  def dateTime: DateTime
  def permutation: Map[BannerPhrase, Position]
  def permutationJMap: JMap[BannerPhrase, Position] = permutation
}

