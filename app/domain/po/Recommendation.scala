package domain.po

import scala.reflect._
import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}


@BeanInfo
case class Recommendation(
  val id: Long = 0,
  val dateTime: DateTime,
  val bannerPhraseBid: Map[domain.BannerPhrase, Double]
)extends domain.Recommendation
{
  def this(dateTime: DateTime, bPhraseBid: JMap[domain.BannerPhrase, Double]) =
    this(0, dateTime = dateTime, bannerPhraseBid = bPhraseBid.toMap)
}

