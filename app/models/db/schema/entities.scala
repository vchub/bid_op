package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.annotations.Column
import java.util.Date
import scala.reflect._

// for Json lib
//import com.fasterxml.jackson.databind.JsonNode
//import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonIgnore}

import AppSchema._
import common._
// fields can be mutable or immutable - val or var


@BeanInfo
case class Network(     //Google, Yandex, etc
  //val id: Long = 0,
  val name: String = ""
  ) { }
object Network {
  def get_by_name(name: String): Network = inTransaction{ networks.where(a => a.name === name).single }
}


@BeanInfo
case class Phrase(
  val id: Long = 0,
  val phrase: String = ""
)extends KeyedEntity[Long]
{
}


@BeanInfo
case class Banner(
  val id: Long = 0,
  val campaign_id: Long = 0 //fk
)extends KeyedEntity[Long]
{
}


@BeanInfo
case class Region(
  val id: Long = 0,
  val region_id: Long = 0, //fk to super Region
  val description: String = ""
)extends KeyedEntity[Long]
{
}



@BeanInfo
case class TimeSlot(
  val id: Long = 0,
  val timeslot_type_id: Long = 0, //fk
  val start_date: Date = new Date(),
  val end_date: Date = new Date()
)extends KeyedEntity[Long]
{
}


@BeanInfo
case class TimeSlotType(
  val id: Long = 0,
  val name: String = ""
)extends KeyedEntity[Long]
{
}


@BeanInfo
case class BannerPhraseStat(
  val id: Long = 0,
  val banner_phrase_id: Long = 0, //fk
  val sum_search: Double = 0,
  val sum_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0,
  val start_date: Date = new Date(), //Note: we'll have to select timeslots in date range
  val end_date: Date = new Date()    //
) extends KeyedEntity[Long]
{
}


@BeanInfo
case class Curve(
  val id: Long = 0,
  val campaign_id: Long = 0, //fk
  val a: Double = 0,
  val b: Double = 0,
  val c: Double = 0,
  val d: Double = 0,
  val start_date: Date = new Date(), //Note: we'll have to select timeslots in date range
  val end_date: Date = new Date()    //
)extends KeyedEntity[Long]
{
}




@BeanInfo
case class Permutation(
  val id: Long = 0,
  val curve_id: Long = 0, //fk
  val timeslot_id: Long = 0, //fk
  val banner_phrase_id: Long = 0, //fk
  val position: Int = 0
)extends KeyedEntity[Long]
{
}




