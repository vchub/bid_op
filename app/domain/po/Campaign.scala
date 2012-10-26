package domain.po

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

  val curves: List[domain.Curve] = Nil,
  val performanceHistory: List[domain.Performance] = Nil,
  val permutationHistory: List[domain.Permutation] = Nil,

  val budgetHistory: List[TSValue[Double]] = Nil,
  val endDateHistory: List[TSValue[DateTime]] = Nil

) extends domain.Campaign
{ }

