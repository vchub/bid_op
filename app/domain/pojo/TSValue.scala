package domain.pojo

import org.joda.time._

/**TimeSeries Value
*/
case class TSValue[T](
  val dateTime: DateTime,
  val elem: T
) extends domain.TSValue[T] with Ordered[TSValue[T]]
{
  def compare(that: TSValue[T]) = dateTime compareTo that.dateTime
}
