package domain.po

import org.joda.time._
import scala.reflect._

@BeanInfo
class PeriodType(
  val id: Long,
  val factor: Double,
  val description: String
) extends domain.PeriodType {}

object PeriodType{
  def apply(dateTime: DateTime): PeriodType = new PeriodType(
      id = 1,
      factor = 1,
      description = ""
    )
}

