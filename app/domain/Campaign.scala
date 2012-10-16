package domain

import scala.reflect._
import org.joda.time._

import _root_.dao.Dao


@BeanInfo
case class Campaign(
  val id: Long,
  val network_campaign_id: String,
  val startDate: DateTime,
  val endDate: Option[DateTime],
  val budget: Option[Double],

  val user: Option[User],
  val network: Option[Network],

  val bannerPhrases: List[BannerPhrase],

  val curves: List[Curve],
  val performanceHistory: List[Performance],
  val permutationHistory: List[Permutation],

  val budgetHistory: List[TSValue[Double]],
  val endDateHistory: List[TSValue[DateTime]],

  // Data access object
  val dao: Option[Dao]
)
{


  // stores new Permutation in DB
  // TODO
  def save(permutation: Permutation): Option[Boolean] = dao map {_.createPermutation(campaign = this, permutation = permutation) }

  // stores new Recommendation in DB
  // TODO
  def saveRecommendation(recommendation: Recommendation): Option[Boolean] =
    dao map {_.createRecommendation(campaign = this, recommendation = recommendation) }

}

