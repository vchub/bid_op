package domain

import scala.reflect._



@BeanInfo
case class Performance(
  val id: Long = 0,
  val sum_search: Double = 0,
  val sum_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0
){}
