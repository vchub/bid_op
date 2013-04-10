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
  def login: Option[String]
  def token: Option[String]

  def bannerPhrases: List[BannerPhrase]
  @transient lazy val bannerPhrasesJList: JList[BannerPhrase] = bannerPhrases

  // start and end Dates of retrieved Campaign Histories
  //@BeanProperty
  def historyStartDate: DateTime
  //@BeanProperty
  def historyEndDate: DateTime

  def curves: List[Curve]
  @transient lazy val curvesJList: JList[Curve] = curves
  def performanceHistory: List[Performance]
  @transient lazy val performanceHistoryJList: JList[Performance] = performanceHistory
  def permutationHistory: List[Permutation]
  @transient lazy val permutationHistoryJList: JList[Permutation] = permutationHistory

  def budgetHistory: List[BudgetHistoryElem]
  @transient lazy val budgetHistoryJList: JList[BudgetHistoryElem] = budgetHistory
  def endDateHistory: List[EndDateHistoryElem]
  @transient lazy val endDateHistoryJList: JList[EndDateHistoryElem] = endDateHistory
}

