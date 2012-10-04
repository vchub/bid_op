package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import java.util.Date
import scala.reflect._

import AppSchema._
import common._


@BeanInfo
case class Recommendation(
  val campaign_id: Long = 0, //fk
  val start_date: Date = new Date() // Recommendation starts be effective. format yyyy-MM-dd-hh-mm-ss
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // Campaign -* Recommendation relation
  lazy val campaign: ManyToOne[Campaign] = AppSchema.campaignRecommendations.right(this)



  /**
  * default put - save to db
  * @param
  * @return Campaign
  **/
  def put(): Recommendation = inTransaction { recommendations insert this }
}

