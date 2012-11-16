package dao.squschema

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.util.Date
import org.joda.time._
import scala.reflect._
import common._



@BeanInfo
case class TimeSlotType(
  val description: String = ""
)extends KeyedEntity[Long]
{
  var id: Long = 0
  var app_schema_factory = new AppSchemaFactory

  // TimeSlot -* TimeSlotType relation
  lazy val timeSlots: OneToMany[TimeSlot] = AppSchema.timeSlotTypeTimeSlots.left(this)



  /**
  * default put - save to db
  * @param
  * @return TimeSlotType
  */
  def put(): TimeSlotType = inTransaction { AppSchema.timeslottypes insert this }

  /**
  * find out TimeSlotType from start_date and end_date
  * @param Date, Date
  * @return TimeSlotType
  */
  // TODO: use joda Enumerations (day of week, weekend, etc)
  def get_timeslottype(start_date: Date, end_date: Date): TimeSlotType = {
    val (s, e) = (new DateTime(start_date), new DateTime(end_date))
    val (sd, ed) = (s.getDayOfWeek, e.getDayOfWeek)
    (sd, ed) match {
      // TODO: add all cases
      case (_,_) if(sd < 6 && ed < 6) => {val t = new TimeSlotType("working day"); t.id = 1; t }
      case _ => {val t = new TimeSlotType("weekend day"); t.id = 2; t }
    }
  }


  //TODO: remove it form here and tests. Used for demo
  def some(id: Int): TimeSlotType => TimeSlotType =
    t => {
      t.id += id
      t
    }


}
object TimeSlotType {

}

