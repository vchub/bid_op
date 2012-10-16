package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Recommendation(
  val date: DateTime,
  val bpBid: Map[BannerPhrase, Double]

){}

