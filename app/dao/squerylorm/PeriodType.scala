package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import org.joda.time._

import scala.reflect._
import common._



@BeanInfo
case class PeriodType(
  val id: Long = 0,
  val factor: Double = 1,
  val description: String = ""
)extends domain.PeriodType with KeyedEntity[Long]
{

  /**
  * default put - save to db
  */
  def put(): PeriodType = inTransaction { AppSchema.periodtypes insert this }
}

object PeriodType {
  def apply(dateTime: DateTime): PeriodType = new PeriodType(
      id = 1,
      factor = 1,
      description = ""
    )
}

