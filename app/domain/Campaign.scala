package domain

import org.joda.time._
import scala.collection.JavaConversions._

trait Campaign{
  def id: Long
  def network_campaign_id: String
  def startDate: DateTime
  def endDate: Option[DateTime]
  def budget: Option[Double]

  def user: Option[User]
  def network: Option[Network]

  def bannerPhrases: List[BannerPhrase]
  def bannerPhrasesJ: java.util.List[BannerPhrase] = bannerPhrases


  def curves: CurveHistory
  def performanceHistory: PerformanceHistory
  def permutationHistory: List[Permutation] //PermutationHistory

  def budgetHistory: BudgetHistory
  def endDateHistory: EndDateHistory
}

