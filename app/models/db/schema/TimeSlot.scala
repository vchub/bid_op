package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import java.util.Date
import scala.reflect._

import common._


import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonIgnore}


@BeanInfo
case class TimeSlot(
  var curve_id: Long = 0, //fk
  var timeslottype_id: Long = 0, //fk
  val start_date: Date = new Date(),
  val end_date: Date = new Date(),
  val sum_search: Double = 0,
  val sum_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0
)extends KeyedEntity[Long]
{
  @transient
  val id: Long = 0

  /* note: still not sure whether it worth to make these IDs variables.
    the point is that we have to assign them separatelly...
  @transient
  var curve_id: Long = 0 //fk
  @transient
  var timeslottype_id: Long = 0 //fk
  */


  @transient
  lazy val curve: ManyToOne[Curve] = AppSchema.curveTimeSlots.right(this)

  // TimeSlotType -* TimeSlot relation
  @transient
  lazy val timeSlotType: ManyToOne[TimeSlotType] = AppSchema.timeSlotTypeTimeSlots.right(this)

  // TODO: it seems that CampaignStats and TimeSlot have 1-1 relation - unite them
  // TimeSlot -* CampaignStats relation
  //lazy val campaignStats: OneToMany[CampaignStats] = AppSchema.timeSlotCampaignStats.left(this)

  // TimeSlot -* Permutation relation
  @transient
  lazy val permutations: OneToMany[Permutation] = AppSchema.timeSlotPermutation.left(this)


  /**
  * default put - save to db
  * @param
  * @return TimeSlot
  */
  def put(): TimeSlot = inTransaction { AppSchema.timeslots insert this }

}

object TimeSlot {

  /**
  * get TimeSlot from DB
  * @return TimeSlot
  */
  def get_by_id(id: Long): TimeSlot = inTransaction{ AppSchema.timeslots.where(a => a.id === id).single }


  /**
  * parse json:String to TimeSlot
  * @return TimeSlot
  */

  def parse(json_string: String): TimeSlot = {
    import com.codahale.jerkson.Json

    val c = Json.parse[TimeSlotJsonCompanion](json_string)

    TimeSlot(start_date = c.start_date, end_date = c.end_date, sum_search = c.sum_search,
      sum_context = c.sum_context, impress_search = c.impress_search, impress_context = c.impress_search,
      clicks_search = c.clicks_search, clicks_context = c.clicks_context
    )

  }
}


    /**
    * Companion class used for parsing from Json
    */
    case class TimeSlotJsonCompanion(
      val start_date: Date = new Date(),
      val end_date: Date = new Date(),
      val sum_search: Double = 0,
      val sum_context: Double = 0,
      val impress_search: Int = 0,
      val impress_context: Int = 0,
      val clicks_search: Int = 0,
      val clicks_context: Int = 0
    ){}

