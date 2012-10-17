package domain.pojo

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Campaign(
  val id: Long,
  val network_campaign_id: String,
  val startDate: DateTime,
  val endDate: Option[DateTime],
  val budget: Option[Double],

  val user: Option[domain.User],
  val network: Option[domain.Network],

  val bannerPhrases: List[domain.BannerPhrase],

  val curves: domain.CurveHistory,
  val performanceHistory: domain.PerformanceHistory,
  val permutationHistory: domain.PermutationHistory,

  val budgetHistory: domain.BudgetHistory,
  val endDateHistory: domain.EndDateHistory
) extends domain.Campaign
{ }

