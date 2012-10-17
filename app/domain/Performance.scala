package domain

import org.joda.time._
import scala.reflect._


trait Performance(
  def id: Long
  def cost_search: Double
  def cost_context: Double
  def impress_search: Int
  def impress_context: Int
  def clicks_search: Int
  def clicks_context: Int

  def periodType: PeriodType
  def date: DateTime   // DateTime of Performance snap-shot
)

@BeanInfo
case class PerformanceD(
  val id: Long,
  val cost_search: Double,
  val cost_context: Double,
  val impress_search: Int,
  val impress_context: Int,
  val clicks_search: Int,
  val clicks_context: Int,
  val periodType: PeriodType,
  val date: DateTime   // DateTime of Performance snap-shot

)extends Performance {}

