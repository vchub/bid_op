package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Campaign(
  val id: Long,
  val startDate: DateTime,
  val endDate: DateTime,
  val budget: Double,
  val banners: Seq[Banner],

  val permutationHistory: PermutationHistory,
  val budgetHistory: BudgetHistory,
  val performanceHistory: PerformanceHistory
){}

