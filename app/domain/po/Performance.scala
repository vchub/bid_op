package domain.po

import org.joda.time._
import scala.reflect._

@BeanInfo
case class Performance(
  val id: Long,
  val cost_search: Double,
  val cost_context: Double,
  val impress_search: Int,
  val impress_context: Int,
  val clicks_search: Int,
  val clicks_context: Int,
  val periodType: domain.PeriodType,
  val dateTime: DateTime   // DateTime of Performance snap-shot

)extends domain.Performance {}

