package domain.pojo

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Curve(
  val id: Long,
  val a: Double,
  val b: Double,
  val c: Double,
  val d: Double,

  val dateTime: DateTime,
  val optimalPermutation: Option[domain.Permutation]

)extends domain.Curve
{}

