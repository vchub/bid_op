package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.util.Date
import scala.reflect._
import common._



@BeanInfo
case class NetAdvisedBidHistory(
  val bannerphrase_id: Long = 0, //fk
  val date: Date = new Date,
  val a: Double = 0,
  val b: Double = 0,
  val c: Double = 0,
  val d: Double = 0
)extends KeyedEntity[Long]
{
  val id: Long = 0

  /**
  * default put - save to db
  **/
  def put(): NetAdvisedBidHistory = inTransaction { AppSchema.netadvisedbidhistory insert this }

}


