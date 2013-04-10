package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.joda.time._
import java.sql.Timestamp
import scala.reflect._
import common._



@BeanInfo
case class ActualBidHistory(
  val bannerphrase_id: Long = 0, //fk
  val date: Timestamp = new Timestamp(0),
  val bid: Double = 0
)extends domain.ActualBidHistoryElem with KeyedEntity[Long] with History
{
  val id: Long = 0
  def elem = bid
  def dateTime = date
  //def dateTime = new DateTime(date)


  /**
  * default put - save to db
  **/
  def put(): ActualBidHistory = inTransaction { AppSchema.actualbidhistory insert this }

}

object ActualBidHistory {
/** construct ActualBidHistory from domain.ActualBidHistory and BannerPhrase
  */
  def apply(bp: domain.BannerPhrase, ab: domain.ActualBidHistoryElem): ActualBidHistory =
    ActualBidHistory(
      bannerphrase_id = bp.id,
      date = ab.dateTime,
      //date = new Timestamp(ab.dateTime.getMillis),
      bid = ab.elem
    )

  /**
  * get ActualBidHistory from DB
  */
  def get_by_id(id: Long): ActualBidHistory = inTransaction{
    AppSchema.actualbidhistory.where(a => a.id === id).single }
}


