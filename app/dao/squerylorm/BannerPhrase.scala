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
)extends KeyedEntity[Long]
{
  val id: Long = 0


  // Banner -* BannerPhrase relation
  lazy val banner: ManyToOne[Banner] = AppSchema.bannerBannerPhrases.right(this)

  // Phrase -* BannerPhrase relation
  lazy val phrase: ManyToOne[Phrase] = AppSchema.phraseBannerPhrases.right(this)

  // Region -* BannerPhrase relation
  lazy val region: ManyToOne[Region] = AppSchema.regionBannerPhrases.right(this)

  // BannerPhrase -* Position relation
  lazy val bannerPhrasePositions: OneToMany[Position] = AppSchema.bannerPhrasePositions.left(this)

  // BannerPhrase -* BannerPhrasePerformance relation
  lazy val bannerPhrasePerformance: OneToMany[BannerPhrasePerformance] = AppSchema.bannerPhrasePerformance.left(this)

  // BannerPhrase -* ActualBidHistory relation
  lazy val bannerPhraseActualBidHistory: OneToMany[ActualBidHistory] = AppSchema.bannerPhraseActualBidHistory.left(this)

  // BannerPhrase -* RecommendationHistory relation
  lazy val bannerPhraseRecommendationHistory: OneToMany[RecommendationHistory] = AppSchema.bannerPhraseRecommendationHistory.left(this)

  // BannerPhrase -* NetAdvisedBidHistory relation
  lazy val bannerPhraseNetAdvisedBidsHistory: OneToMany[NetAdvisedBidHistory] = AppSchema.bannerPhraseNetAdvisedBidsHistory.left(this)




  /** default put - save to db
  **/
  def put(): BannerPhrase = inTransaction { AppSchema.bannerphrases insert this }


  /** creates domain.BannerPhrase
  **/
  // TODO: Optimize
  def domainBannerPhrase(): domain.BannerPhrase = inTransaction {
    domain.BannerPhrase(
      id = id,
      banner = banner.headOption map(_.domainBanner),
      phrase = phrase.headOption map(_.domainPhrase),
      region = region.headOption map(_.domainRegion),

      //TODO: add Date filter
      actualBidHistory = bannerPhraseActualBidHistory.toList map((x: ActualBidHistory) =>
          domain.TSValue(date = new DateTime(x.date), elem = x.bid)),
      recommendationHistory = bannerPhraseRecommendationHistory.toList map((x: RecommendationHistory) =>
          domain.TSValue(new DateTime(x.date), x.bid)),
      netAdvisedBidsHistory = bannerPhraseNetAdvisedBidsHistory.toList map((x: NetAdvisedBidHistory) =>
          domain.TSValue(new DateTime(x.date), domain.NetAdvisedBids(x.a, x.b, x.c, x.d))),
      performanceHistory = bannerPhrasePerformance.toList
    )
  }



}

object BannerPhrase {


}

