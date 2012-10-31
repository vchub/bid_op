package domain

import org.joda.time._




/**TimeSeries Value
*/
trait TSValue[T] {
  def dateTime: DateTime
  def elem: T
}

trait ActualBidHistoryElem extends TSValue[Double]
trait BudgetHistoryElem extends TSValue[Double]
trait RecommendationHistoryElem extends TSValue[Double]
trait EndDateHistoryElem extends TSValue[DateTime]


