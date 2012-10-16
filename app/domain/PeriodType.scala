package domain

import scala.reflect._


@BeanInfo
case class PeriodType(
  val id: Long,
  val factor: Double
) {}

