package domain

import org.joda.time._


trait Recommendation
{
  def dateTime: DateTime
  def bannerPhraseBid: Map[BannerPhrase, Double]
}

