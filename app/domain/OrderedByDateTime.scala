package domain

import org.joda.time._

/**Ordered by dateTime: DateTime element of Class
*/
trait OrderedByDateTime[T] extends Ordered[OrderedByDateTime[T]] {
  def dateTime: DateTime
  def compare(that: OrderedByDateTime[T]) = this.dateTime compareTo that.dateTime
}
