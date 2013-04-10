package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.lang.RuntimeException
import java.util.Date
import org.joda.time._
import java.sql.Timestamp

import scala.reflect._



@BeanInfo
case class CheckTime(
  val elem: Double = 0,
  val dateDate: Date = new Date(),
  val date: Timestamp = new Timestamp(0)
)extends domain.CheckTime with KeyedEntity[Long]
{
  val id: Long = 0
  def dateTime = new DateTime(date)



  def put(): CheckTime = inTransaction { AppSchema.checktimes insert this }



}
object CheckTime {


  /**
  * get Curve from DB
  * @return Curve
  */
  def get_by_id(id: Long): Option[ CheckTime ] = inTransaction{ AppSchema.checktimes.where(a => a.id === id).headOption}
}
