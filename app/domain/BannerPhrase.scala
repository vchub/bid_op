package domain

import scala.reflect._


@BeanInfo
case class BannerPhrase(
  val id: Long = 0,
  val banner: Option[Banner] = None,
  val phrase: Option[Phrase] = None,
  val region: Option[Region] = None,
  val actualBidHistory: Seq[TimeSeries] = Seq(),
  val recommendedBidHistory: Seq[TimeSeries] = Seq(),

  val performanceHistory: Option[PerformanceHistory] = None
) {}

