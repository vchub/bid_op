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
  lazy val bannerPhrasesJList: JList[BannerPhrase] = bannerPhrases

  // start and end Dates of retrieved Campaign Histories
  //@BeanProperty
  def historyStartDate: DateTime
  //@BeanProperty
  def historyEndDate: DateTime

  def curves: List[Curve]
  lazy val curvesJList: JList[Curve] = curves
  def performanceHistory: List[Performance]
  lazy val performanceHistoryJList: JList[Performance] = performanceHistory
  def permutationHistory: List[Permutation]
  lazy val permutationHistoryJList: JList[Permutation] = permutationHistory

  def budgetHistory: List[BudgetHistoryElem]
  lazy val budgetHistoryJList: JList[BudgetHistoryElem] = budgetHistory
  def endDateHistory: List[EndDateHistoryElem]
  lazy val endDateHistoryJList: JList[EndDateHistoryElem] = endDateHistory
}

