package domain.po

import scala.reflect._
import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}


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

  val budgetHistory: List[domain.BudgetHistoryElem] = Nil,
  val endDateHistory: List[domain.EndDateHistoryElem] = Nil

) extends domain.Campaign
{}
object Campaign {
  def apply(
    id: Long,
    network_campaign_id: String,
    startDate: DateTime,
    endDate: Option[DateTime],
    budget: Option[Double],

    user: Option[domain.User],
    network: Option[domain.Network],

    bannerPhrases: JList[domain.BannerPhrase],

    historyStartDate: DateTime,
    historyEndDate: DateTime,

    curves: JList[domain.Curve],
    performanceHistory: JList[domain.Performance],
    permutationHistory: JList[domain.Permutation],

    budgetHistory: JList[domain.BudgetHistoryElem],
    endDateHistory: JList[domain.EndDateHistoryElem]
  ):Campaign = Campaign(
      id = id,
      network_campaign_id = network_campaign_id,
      startDate = startDate,
      endDate = endDate,
      budget = budget,

      user = user,
      network = network,

      bannerPhrases = bannerPhrases.toList,

      historyStartDate = historyStartDate,
      historyEndDate = historyEndDate,

      curves = curves.toList,
      performanceHistory = performanceHistory.toList,
      permutationHistory = permutationHistory.toList,

      budgetHistory = budgetHistory.toList,
      endDateHistory = endDateHistory.toList
    )



}

