package dao.squerylorm

import org.squeryl.{ Schema, KeyedEntity, Query }
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.squeryl.annotations.Transient
import org.joda.time._
import java.sql.Timestamp
import scala.reflect._
import common._

@BeanInfo
case class BannerPhrase(
  val campaign_id: Long = 0, //fk
  val banner_id: Long = 0, //fk
  val phrase_id: Long = 0, //fk
  val region_id: Long = 0 //fk
  ) extends domain.BannerPhrase with KeyedEntity[Long] {
  val id: Long = 0

  @Transient
  var campaign: Option[domain.Campaign] = None

  def banner = inTransaction { bannerRel.headOption }
  def phrase = inTransaction { phraseRel.headOption }
  def region = inTransaction { regionRel.headOption }

  // BannerPhrase History in ascending order and in conformance to campaign.historyStartDate, historyEndDate
  def getBannerPhraseHistory[T <: History](qRel: Query[T]): List[T] = campaign match {
    case None => Nil
    case Some(campaign) if (campaign.historyStartDate != campaign.historyEndDate) => inTransaction {
      from(qRel)((b) =>
        where(b.date >= convertToJdbc(campaign.historyStartDate)
          and b.date <= convertToJdbc(campaign.historyEndDate))
          select (b) orderBy (b.date asc)).toList
    }
    case _ => Nil
  }

  //get History using Campaign.historyStartDate and historyEndDate
  lazy val actualBidHistory = getBannerPhraseHistory[ActualBidHistory](bannerPhraseActualBidHistoryRel)

  lazy val recommendationHistory = getBannerPhraseHistory[RecommendationHistory](bannerPhraseRecommendationHistoryRel)

  lazy val netAdvisedBidsHistory = getBannerPhraseHistory[NetAdvisedBidHistory](bannerPhraseNetAdvisedBidsHistoryRel)
  /*
  def netAdvisedBidsHistory = bannerPhraseNetAdvisedBidsHistoryRel.toList map((x: NetAdvisedBidHistory) =>
      domain.TSValue(new DateTime(x.date), domain.NetAdvisedBids(x.a, x.b, x.c, x.d)))
  */

  //get History using Campaign.historyStartDate and historyEndDate
  lazy val performanceHistory = getBannerPhraseHistory[BannerPhrasePerformance](bannerPhrasePerformanceRel)

  // Campaign -* BannerPhrase relation
  lazy val campaignRel: ManyToOne[Campaign] = AppSchema.campaignBannerPhrases.right(this)

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

  /**
   * default put - save to db
   */
  def put(): BannerPhrase = inTransaction { AppSchema.bannerphrases insert this }

}

object BannerPhrase {

  /**
   * select BannerPhrase for given Campaign, network_banner_id, network_phrase_id and network_region_id
   * it should be 1 BannerPhrase
   * @param Campaign, String, String, String
   * @return BannerPhrase
   */
  def select(campaign: Campaign, network_banner_id: String, network_phrase_id: String,
    network_region_id: String): List[BannerPhrase] = inTransaction {
    from(AppSchema.bannerphrases, AppSchema.banners, AppSchema.phrases, AppSchema.regions)((bp, b, ph, r) =>
      where(
        bp.campaign_id === campaign.id and
          bp.banner_id === b.id and
          bp.phrase_id === ph.id and
          bp.region_id === r.id and
          b.network_banner_id === network_banner_id and
          ph.network_phrase_id === network_phrase_id and
          r.network_region_id === network_region_id) select (bp)).toList
  }

  /**
   * select BannerPhrase for given Campaign, bannerphrase_id
   * it should be 1 BannerPhrase
   * @param Campaign, Long
   * @return BannerPhrase
   */ 
  def select(campaign: Campaign, bannerphrase_id: Long): Option[BannerPhrase] = inTransaction {
    val bpOpt = from(AppSchema.bannerphrases)(bp =>
      where(
        bp.campaign_id === campaign.id and
          bp.id === bannerphrase_id) select (bp)).headOption
    bpOpt map { bp =>
      bp.campaign = Some(campaign)
      Some(bp)
    } getOrElse None
  }
}

/**
 * def apply(c: domain.Campaign, b: domain.Banner, p: domain.Phrase, r: domain.Region): BannerPhrase =
    BannerPhrase(
      campaign_id = c.id,
      banner_id = b.id,
      phrase_id = p.id,
      region_id = r.id)
**/
