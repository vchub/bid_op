package domain

import org.joda.time._

/** Bids that we receive from Net or clients
* assess "guaranteed buying"
*/

trait NetAdvisedBids extends OrderedByDateTime[NetAdvisedBids]{
  //Cost per impression in Guaranteed Placement - Yandex API - min
  def a:Double
  //Cost per impression in the 1st Place position - Yandex API - max
  def b:Double
  //Cost per impression in Premium Placement - Yandex API - PremiumMin
  def c:Double
  //Cost for an impression in the top position in Premium Placement.  - Yandex API - PremiumMax
  def d:Double

  // reserved for future use
  def e:Double
  // reserved for future use
  def f:Double
  def dateTime: DateTime
}

