package domain

import org.joda.time._

/** Bids that we receive from Net or clients
* assess "guaranteed buying"
*/

trait NetAdvisedBids extends OrderedByDateTime[NetAdvisedBids]{
  def a:Double
  def b:Double
  def c:Double
  def d:Double
  def dateTime: DateTime
}

