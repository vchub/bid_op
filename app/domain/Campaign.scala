package domain

import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}

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


  def curves: List[Curve]
  def curvesJList: JList[Curve] = curves
  def performanceHistory: List[Performance]
  def performanceHistoryJList: JList[Performance] = performanceHistory
  def permutationHistory: List[Permutation]
  def permutationHistoryJList: JList[Permutation] = permutationHistory

  def budgetHistory: List[TSValue[Double]]
  def budgetHistoryJList: JList[TSValue[Double]] = budgetHistory
  def endDateHistory: List[TSValue[DateTime]]
  def endDateHistoryJList: JList[TSValue[DateTime]] = endDateHistory
}

