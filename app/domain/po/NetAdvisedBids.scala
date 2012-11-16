package domain.po

import org.joda.time._

/** Bids that we receive from Net or clients
* assess "guaranteed buying"
*/


case class NetAdvisedBids(
  val a:Double,
  val b:Double,
  val c:Double,
  val d:Double,
  val e:Double,
  val f:Double,
  val dateTime: DateTime
)extends domain.NetAdvisedBids{}

