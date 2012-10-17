package domain.pojo

import scala.reflect._
import org.joda.time._


/** Permutation happens at DateTime and associated w/ Curve
*/
@BeanInfo
case class Permutation(
  val dateTime: DateTime,
  //val curve: Curve,
  val permutation: Map[domain.BannerPhrase, domain.Position]
) extends domain.Permutation {}

