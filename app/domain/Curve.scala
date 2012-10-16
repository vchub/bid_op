package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Curve(
  val id: Long,
  val a: Double,
  val b: Double,
  val c: Double,
  val d: Double,

  val date: DateTime,
  val optimalPermutation: Option[Permutation]

){}

