package domain

import org.joda.time._
import scala.reflect._



@BeanInfo
case class Performance(
  val id: Long = 0,
  val cost_search: Double = 0,
  val cost_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0,

  val ext_bid_1: Double = 0,
  val ext_bid_2: Double = 0,
  val ext_bid_3: Double = 0,
  val ext_bid_4: Double = 0,

  val periodType: Option[PeriodType] = None,
  val date: DateTime = new DateTime   // DateTime of Performance snap-shot

){}

