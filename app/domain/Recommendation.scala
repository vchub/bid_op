package domain

import org.joda.time._
import scala.collection.JavaConversions._
import java.util.{Map => JMap, List => JList}


trait Recommendation
{
  def id: Long
  def dateTime: DateTime
  def bannerPhraseBid: Map[BannerPhrase, Double]
  lazy val bannerPhraseBidJMap: JMap[BannerPhrase, Double] = bannerPhraseBid
}

