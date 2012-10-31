package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.joda.time._
import java.sql.Timestamp
import scala.reflect._
import common._



@BeanInfo
case class EndDateHistory(
  val campaign_id: Long = 0, //fk
  val endDate: Timestamp = new Timestamp(0),
  val date: Timestamp = new Timestamp(0)
)extends domain.EndDateHistoryElem with KeyedEntity[Long] with History
{
  val id: Long = 0
  def elem = endDate
  def dateTime = date

  /**
  * default put - save to db
  **/
  def put(): EndDateHistory = inTransaction { AppSchema.enddatehistory insert this }


}


