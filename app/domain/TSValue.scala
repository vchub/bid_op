package domain

import org.joda.time._

/**TimeSeries Value
*/
trait TSValue[T]
{
  def dateTime: DateTime
  def elem: T
}
