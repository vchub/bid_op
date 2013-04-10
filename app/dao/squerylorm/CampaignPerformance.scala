package dao.squerylorm

import org.squeryl.{ Schema, KeyedEntity }
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
  val date: Timestamp = new Timestamp(0)) extends domain.Performance with KeyedEntity[Long] with History {
  val id: Long = 0

  def dateTime = date
  // TODO: optimize... it should be no direct access to DB
  def periodType: domain.PeriodType = inTransaction { periodTypeRel.head }

  // PeriodType -* CampaignPerformance relation
  lazy val periodTypeRel: ManyToOne[PeriodType] = AppSchema.periodTypeCampaignPerformance.right(this)

  /**
   * put - save to db
   */
  def put(): CampaignPerformance = inTransaction { AppSchema.campaignperformance insert this }

}

object CampaignPerformance {

  /**
   * get CampaignPerformance from DB
   */
  def get_by_id(id: Long): CampaignPerformance = inTransaction {
    AppSchema.campaignperformance.where(a => a.id === id).single
  }

  def apply(c: domain.Campaign, p: domain.Performance): CampaignPerformance = {
    //p.dateTime must be between 00:00:00 and 23:59:59
    //current day performance List
    val camp = Campaign.get_by_id(c.id)
    camp.historyStartDate = p.dateTime.minusMillis(p.dateTime.getMillisOfDay()) //00:00:00
    camp.historyEndDate = camp.historyStartDate.plusDays(1) //current day

    val perf = camp.performanceHistory //only today performance

    CampaignPerformance(
      campaign_id = c.id,
      periodtype_id = p.periodType.id,
      cost_search = p.cost_search - perf.map(_.cost_search).sum,
      cost_context = p.cost_context - perf.map(_.cost_context).sum,
      impress_search = p.impress_search - perf.map(_.impress_search).sum,
      impress_context = p.impress_context - perf.map(_.impress_context).sum,
      clicks_search = p.clicks_search - perf.map(_.clicks_search).sum,
      clicks_context = p.clicks_context - perf.map(_.clicks_context).sum,
      date = p.dateTime)
  }
}


