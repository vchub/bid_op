package domain

import scala.reflect._


@BeanInfo
case class PeriodType(
  val id: Long = 0,
  val factor: Double = 1
) {}

