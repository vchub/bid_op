package dao.squerylorm

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.joda.time._
import scala.reflect._
import common._


@BeanInfo
case class BannerPhrase(
  val banner_id: Long = 0, //fk
  val phrase_id: Long = 0, //fk
  val region_id: Long = 0 //fk
)extends domain.BannerPhrase with KeyedEntity[Long]
{
  val id: Long = 0

  def banner = bannerRel.headOption
  def phrase = phraseRel.headOption
  def region = regionRel.headOption

  //TODO: add Date filter
  def actualBidHistory = inTransaction{ bannerPhraseActualBidHistoryRel.toList }
  /*
  map((x: ActualBidHistory) =>
      domain.TSValue(dateTime = new DateTime(x.date), elem = x.bid))
  */

  def recommendationHistory = inTransaction{ bannerPhraseRecommendationHistoryRel.toList }
  /*
  map((x: RecommendationHistory) =>
      domain.TSValue(new DateTime(x.date), x.bid))
  */

  def netAdvisedBidsHistory = inTransaction{ bannerPhraseNetAdvisedBidsHistoryRel.toList }
  /*
  def netAdvisedBidsHistory = bannerPhraseNetAdvisedBidsHistoryRel.toList map((x: NetAdvisedBidHistory) =>
      domain.TSValue(new DateTime(x.date), domain.NetAdvisedBids(x.a, x.b, x.c, x.d)))
  */

  def performanceHistory = inTransaction{ bannerPhrasePerformanceRel.toList }

  // Banner -* BannerPhrase relation
  lazy val bannerRel: ManyToOne[Banner] = AppSchema.bannerBannerPhrases.right(this)

  // Phrase -* BannerPhrase relation
  lazy val phraseRel: ManyToOne[Phrase] = AppSchema.phraseBannerPhrases.right(this)

  // Region -* BannerPhrase relation
  lazy val regionRel: ManyToOne[Region] = AppSchema.regionBannerPhrases.right(this)

  // BannerPhrase -* Position relation
  lazy val bannerPhrasePositionsRel: OneToMany[Position] = AppSchema.bannerPhrasePositions.left(this)

  // BannerPhrase -* BannerPhrasePerformance relation
  lazy val bannerPhrasePerformanceRel: OneToMany[BannerPhrasePerformance] = AppSchema.bannerPhrasePerformance.left(this)

  // BannerPhrase -* ActualBidHistory relation
  lazy val bannerPhraseActualBidHistoryRel: OneToMany[ActualBidHistory] = AppSchema.bannerPhraseActualBidHistory.left(this)

  // BannerPhrase -* RecommendationHistory relation
  lazy val bannerPhraseRecommendationHistoryRel: OneToMany[RecommendationHistory] = AppSchema.bannerPhraseRecommendationHistory.left(this)

  // BannerPhrase -* NetAdvisedBidHistory relation
  lazy val bannerPhraseNetAdvisedBidsHistoryRel: OneToMany[NetAdvisedBidHistory] = AppSchema.bannerPhraseNetAdvisedBidsHistory.left(this)




  /** default put - save to db
  **/
  def put(): BannerPhrase = inTransaction { AppSchema.bannerphrases insert this }


}

object BannerPhrase {
}
