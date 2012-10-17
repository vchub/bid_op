package domain.pojo

import scala.reflect._

@BeanInfo
case class PeriodType(
  val id: Long,
  val factor: Double,
  val description: String
) extends domain.PeriodType {}

