package domain
package po

import scala.reflect._
import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}


/** Permutation happens at DateTime and associated w/ Curve
*/
@BeanInfo
case class Permutation(
  val id: Long = 0,
  val dateTime: DateTime,
  val permutation: Map[domain.BannerPhrase, domain.Position]
) extends domain.Permutation
{
  def this(dateTime: DateTime, perm: JMap[domain.BannerPhrase, domain.Position]) =
    this(id =0, dateTime = dateTime, permutation = perm.toMap)
}

