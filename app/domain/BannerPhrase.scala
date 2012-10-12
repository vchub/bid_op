package domain

import scala.reflect._


@BeanInfo
case class BannerPhrase(
  val id: Long,
  val banner: Banner,
  val phrase: Phrase,
  val region: Region,
  val actualBidHistory: RecommendationHistory,
  val recommendationHistory: ActualBidHistory,

  val performanceHistory: PerformanceHistory
) {}

