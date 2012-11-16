package serializers

import org.joda.time._
import com.codahale.jerkson.Json

import dao.squerylorm.SquerylDao

case class Performance(
  val start_date: DateTime,
  val end_date: DateTime,
  val sum_search: Double,
  val sum_context: Double,
  val impress_search: Int,
  val impress_context: Int,
  val clicks_search: Int,
  val clicks_context: Int

)extends domain.Performance {
  val id: Long = 0
  val cost_search = sum_search
  val cost_context = sum_context

  val dateTime: DateTime = end_date   // DateTime of Performance snap-shot

  //TODO: PeriodType should be changed from dummy one. Make change in Dao
  @transient
  var periodType: domain.PeriodType = (new SquerylDao).getPeriodType(dateTime)
}

object Performance {

  /** Constructor from domain.Performance
  */
  def apply(p: domain.Performance): Performance = {
    val performance = Performance(
      start_date = p.dateTime,
      end_date = p.dateTime,
      sum_search = p.cost_search ,
      sum_context = p.cost_context,
      impress_search = p.impress_search ,
      impress_context = p.impress_context ,
      clicks_search = p.clicks_search ,
      clicks_context = p.clicks_context
    )
    performance
  }

  /** Constructor from JSON as String
  * it needs Dao to find out the PeriodType
  */
  def apply(jsonString: String, pdao: dao.Dao = new SquerylDao): Performance ={
    val perf = Json.parse[Performance](jsonString)
    perf.periodType = pdao.getPeriodType(perf.dateTime)
    perf
  }


}
