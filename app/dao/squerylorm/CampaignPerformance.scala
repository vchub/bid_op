package dao.squerylorm

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.joda.time._
import java.sql.Timestamp
import scala.reflect._
import common._


@BeanInfo
case class CampaignPerformance(
  val campaign_id: Long = 0, //fk
  val periodtype_id: Long = 0, //fk
  val cost_search: Double = 0,
  val cost_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0,
  val date: Timestamp = new Timestamp(0)
) extends domain.Performance with KeyedEntity[Long] with History
{
  val id: Long = 0

  def dateTime = date
  // TODO: optimize... it should be no direct access to DB
  def periodType: domain.PeriodType = inTransaction{ periodTypeRel.head }

  // PeriodType -* CampaignPerformance relation
  lazy val periodTypeRel: ManyToOne[PeriodType] = AppSchema.periodTypeCampaignPerformance.right(this)


  /**
  * put - save to db
  **/
  def put(): CampaignPerformance = inTransaction { AppSchema.campaignperformance insert this }


}

object CampaignPerformance {

  /**
  * get CampaignPerformance from DB
  */
  def get_by_id(id: Long): CampaignPerformance = inTransaction{
    AppSchema.campaignperformance.where(a => a.id === id).single }



  def apply(c: domain.Campaign, p: domain.Performance): CampaignPerformance = CampaignPerformance(
    campaign_id = c.id,
    periodtype_id = p.periodType.id,
    cost_search = p.cost_search,
    cost_context = p.cost_context,
    impress_search = p.impress_search,
    impress_context = p.impress_context,
    clicks_search = p.clicks_search,
    clicks_context = p.clicks_context,
    date = p.dateTime
  )

}


