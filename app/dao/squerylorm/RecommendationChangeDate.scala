package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.sql.Timestamp
import org.joda.time._
import scala.reflect._
import common._



@BeanInfo
case class RecommendationChangeDate(
  val campaign_id: Long = 0, //fk
  val date: Timestamp = new Timestamp(0)
)extends KeyedEntity[Long]
{
  val id: Long = 0
  def dateTime = date

  /**
  * default put - save to db
  **/
  def put(): RecommendationChangeDate = inTransaction { AppSchema.recommendationchangedate insert this }

}
object RecommendationChangeDate {


  /** check if Recommendation has changed since dateTime */
  def recommendationChangedSince(campaign_id: Long, dateTime: DateTime): Boolean = inTransaction {
    from(AppSchema.recommendationchangedate)(r =>
      where(r.campaign_id === campaign_id)
      select(r.dateTime)
    ).headOption match {
      case None => false
      case Some(date) => date isAfter dateTime
    }
  }

}


