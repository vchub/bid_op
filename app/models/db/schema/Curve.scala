package models.db
package schema

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import java.lang.RuntimeException
import java.util.Date

import scala.reflect._



@BeanInfo
case class Curve(
  val campaign_id: Long = 0, //fk
  val a: Double = 0,
  val b: Double = 0,
  val c: Double = 0,
  val d: Double = 0,
  val start_date: Date = new Date() //Timestamp = new Timestamp(0), //Note: we'll have to select timeslots in date range
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // Campaign -* Curve relation
  lazy val campaign: ManyToOne[Campaign] = AppSchema.campaignCurves.right(this)

  // Curve -* TimeSlot relation
  lazy val timeSlots: OneToMany[TimeSlot] = AppSchema.curveTimeSlots.left(this)




  def put(): Curve = inTransaction { AppSchema.curves insert this }
}
object Curve {


  /**
  * get Curve from DB
  * @return Curve
  */
  def get_by_id(id: Long): Campaign = inTransaction{ AppSchema.campaigns.where(a => a.id === id).single }
}
