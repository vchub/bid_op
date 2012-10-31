package domain

import scala.reflect._
import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}

@BeanInfo
trait Campaign{
  def id: Long
  def network_campaign_id: String
  def startDate: DateTime
  def endDate: Option[DateTime]
  def budget: Option[Double]

  def user: Option[User]
  def network: Option[Network]

  def bannerPhrases: List[BannerPhrase]
  def bannerPhrasesJList: JList[BannerPhrase] = bannerPhrases

  // start and end Dates of retrieved Campaign Histories
  @BeanProperty
  var historyStartDate: DateTime = new DateTime
  @BeanProperty
  var historyEndDate: DateTime = new DateTime

  def curves: List[Curve]
  def curvesJList: JList[Curve] = curves
  def performanceHistory: List[Performance]
  def performanceHistoryJList: JList[Performance] = performanceHistory
  def permutationHistory: List[Permutation]
  def permutationHistoryJList: JList[Permutation] = permutationHistory

  def budgetHistory: List[BudgetHistoryElem]
  def budgetHistoryJList: JList[BudgetHistoryElem] = budgetHistory
  def endDateHistory: List[EndDateHistoryElem]
  def endDateHistoryJList: JList[EndDateHistoryElem] = endDateHistory
}

