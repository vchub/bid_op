package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.sql.Timestamp
import org.joda.time._
import scala.reflect._
import common._



@BeanInfo
case class RecommendationHistory(
  val bannerphrase_id: Long = 0, //fk
  val date: Timestamp = new Timestamp(0),
  val bid: Double = 0
)extends domain.RecommendationHistoryElem with KeyedEntity[Long] with History
{
  val id: Long = 0
  def elem = bid
  def dateTime = date

  /**
  * default put - save to db
  **/
  def put(): RecommendationHistory = inTransaction { AppSchema.recommendationhistory insert this }

}
object RecommendationHistory {

  /** creates RecommendationHistory record
  */
  def create(recommendation: domain.Recommendation): Unit = inTransaction{
    val rec_l = recommendation.bannerPhraseBid map {case (bp, bid) =>
      RecommendationHistory(bannerphrase_id = bp.id, date = recommendation.dateTime, bid = bid) }
    AppSchema.recommendationhistory.insert(rec_l)
  }

}


