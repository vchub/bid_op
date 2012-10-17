package domain.pojo

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Recommendation(
  val dateTime: DateTime,
  val bannerPhraseBid: Map[domain.BannerPhrase, Double]

)extends domain.Recommendation
{}

