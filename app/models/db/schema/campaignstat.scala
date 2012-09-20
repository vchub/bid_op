package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import scala.reflect._

import AppSchema._
import common._


@BeanInfo
case class CampaignStats(
  val id: Long = 0,
  val campaign_id: Long = 0, //fk
  val timeslot_id: Long = 0, //fk
  val sum_search: Double = 0,
  val sum_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0
) extends KeyedEntity[Long]
{
}
