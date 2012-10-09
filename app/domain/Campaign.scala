package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Campaign(
  val id: Long = 0,
  val startDate: DateTime = new DateTime,
  val endDate: DateTime = new DateTime,
  val budget: Double = 0,

  val curves: Seq[Curve] = Seq(),
  val banners: Seq[Banner] = Seq(),
  val budgetHistory: Seq[TimeSeries] = Seq(),
  val performanceHistory: Option[PerformanceHistory] = None
){}

