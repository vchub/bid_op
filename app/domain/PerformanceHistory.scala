package domain

import scala.reflect._
import scala.collection.immutable.List
import org.joda.time._


@BeanInfo
class PerformanceHistory(
  val seq: Seq[Performance] = Seq()
) {}

