package domain

import scala.reflect._
import scala.collection.immutable.List
import org.joda.time._


@BeanInfo
class NetAdvisedBidsHistory(
  val seq: Seq[( DateTime, NetAdvisedBids )] = Seq()
){}

case class NetAdvisedBids(val a:Double, val b:Double, val c:Double, val d:Double){}

