package domain

import scala.reflect._


@BeanInfo
class PerformanceHistory(
  val seq: Seq[Performance] = Seq()
) {}

