package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._
import common._



@BeanInfo
case class PeriodType(
  val factor: Double = 1,
  val description: String = ""
)extends domain.PeriodType with KeyedEntity[Long]
{
  var id: Long = 0

  /**
  * default put - save to db
  */
  def put(): PeriodType = inTransaction { AppSchema.periodtypes insert this }

  /** creates domain.Performance
  **/
  /*
  def domainPeriodType(): domain.PeriodType = domain.PeriodType(
    id = id,
    factor = factor
  )
  */

}
object TimeSlotType {

}

