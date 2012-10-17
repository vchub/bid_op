package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.util.Date
import org.joda.time._
import scala.reflect._
import common._



@BeanInfo
case class ActualBidHistory(
  val bannerphrase_id: Long = 0, //fk
  val date: Date = new Date,
  val bid: Double = 0
)extends domain.TSValue[Double] with KeyedEntity[Long]
{
  val id: Long = 0
  def elem = bid
  def dateTime = new DateTime(date)


  /**
  * default put - save to db
  **/
  def put(): ActualBidHistory = inTransaction { AppSchema.actualbidhistory insert this }

}


