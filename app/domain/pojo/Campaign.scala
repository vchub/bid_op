package domain.pojo

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Campaign(
  val id: Long = 0,
  val network_campaign_id: String = "",
  val startDate: DateTime = new DateTime,
  val endDate: Option[DateTime] = Some(new DateTime),
  val budget: Option[Double] = Some(0.0),

  val user: Option[domain.User] = None,
  val network: Option[domain.Network] = None,

  val bannerPhrases: List[domain.BannerPhrase] = Nil,

  val curves: domain.CurveHistory = Nil,
  val performanceHistory: domain.PerformanceHistory = Nil,
  val permutationHistory: domain.PermutationHistory = Nil,

  val budgetHistory: domain.BudgetHistory = Nil,
  val endDateHistory: domain.EndDateHistory = Nil
) extends domain.Campaign
{ }

