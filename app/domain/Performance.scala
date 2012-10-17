package domain

import org.joda.time._


trait Performance{
  def id: Long
  def cost_search: Double
  def cost_context: Double
  def impress_search: Int
  def impress_context: Int
  def clicks_search: Int
  def clicks_context: Int

  def periodType: PeriodType
  def dateTime: DateTime   // DateTime of Performance snap-shot
}
