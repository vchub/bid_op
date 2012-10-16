package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.util.Date
import org.joda.time._
import scala.reflect._
import common._



@BeanInfo
case class EndDateHistory(
  val campaign_id: Long = 0, //fk
  val endDate: Date = new Date,
  val date: Date = new Date
)extends KeyedEntity[Long]
{
  val id: Long = 0

  /**
  * default put - save to db
  **/
  def put(): EndDateHistory = inTransaction { AppSchema.enddatehistory insert this }

  /** creates domain.TSValue
  **/
  def domainTSValue(): domain.TSValue[DateTime] = domain.TSValue(
      date = new DateTime(date),
      elem = new DateTime(endDate)
    )

}


