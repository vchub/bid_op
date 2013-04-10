package domain
package po

import scala.reflect._
import org.joda.time._

/**TimeSeries Value
*/
@BeanInfo
case class TSValue[T](
  val dateTime: DateTime,
  val elem: T
)extends domain.TSValue[T] with Ordered[TSValue[T]]
{
  def compare(that: TSValue[T]) = dateTime compareTo that.dateTime
}


@BeanInfo
case class BudgetHistoryElem(
  val dateTime: DateTime,
  val elem: Double
)extends domain.BudgetHistoryElem with Ordered[BudgetHistoryElem]
{
  def compare(that: BudgetHistoryElem) = dateTime compareTo that.dateTime
}


@BeanInfo
case class ActualBidHistoryElem(
  val dateTime: DateTime,
  val elem: Double
)extends domain.ActualBidHistoryElem with Ordered[ActualBidHistoryElem]
{
  def compare(that: ActualBidHistoryElem) = dateTime compareTo that.dateTime
}


@BeanInfo
case class RecommendationHistoryElem(
  val dateTime: DateTime,
  val elem: Double
)extends domain.RecommendationHistoryElem with Ordered[RecommendationHistoryElem]
{
  def compare(that: RecommendationHistoryElem) = dateTime compareTo that.dateTime
}


@BeanInfo
case class EndDateHistoryElem(
  val dateTime: DateTime,
  val elem: DateTime
)extends domain.EndDateHistoryElem with Ordered[EndDateHistoryElem]
{
  def compare(that: EndDateHistoryElem) = dateTime compareTo that.dateTime
}

