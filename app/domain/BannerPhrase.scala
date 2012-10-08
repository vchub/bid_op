package domain

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._
import common._
import domain.dao.AppSchema


@BeanInfo
case class BannerPhrase(
  val banner_id: Long = 0, //fk
  val phrase_id: Long = 0, //fk
  val region_id: Long = 0, //fk
  val bid: Double = 0       // bid or price for cpc or rtb
)extends KeyedEntity[Long]
{
  val id: Long = 0


  // Banner -* BannerPhrase relation
  lazy val banner: ManyToOne[Banner] = AppSchema.bannerBannerPhrases.right(this)

  // Phrase -* BannerPhrase relation
  lazy val phrase: ManyToOne[Phrase] = AppSchema.phraseBannerPhrases.right(this)

  // Region -* BannerPhrase relation
  lazy val region: ManyToOne[Region] = AppSchema.regionBannerPhrases.right(this)

  // BannerPhrase -* BannerPhraseStats relation
  lazy val bannerPhraseStats: OneToMany[BannerPhraseStats] = AppSchema.bannerPhraseBannerPhraseStats.left(this)

  // BannerPhrase -* Permutation relation
  lazy val bannerPhrasePermutatons: OneToMany[Permutation] = AppSchema.bannerPhrasePermutatons.left(this)



  /**
  * default put - save to db
  * @param
  * @return BannerPhrase
  **/
  def put(): BannerPhrase = inTransaction { AppSchema.bannerphrases insert this }
}

object BannerPhrase {

  /**
  * select BannerPhrase for given Campaign, network_banner_id, network_phrase_id and network_region_id
  * it should be 1 BannerPhrase
  * @param Campaign, String, String, String
  * @return BannerPhrase
  **/
  def select(campaign: Campaign, network_banner_id: String, network_phrase_id: String,
    network_region_id: String ): List[BannerPhrase] = inTransaction {
      from(AppSchema.bannerphrases, AppSchema.banners, AppSchema.phrases, AppSchema.regions)((bp, b, ph, r) =>
        where(
          b.campaign_id === campaign.id and
          bp.banner_id === b.id and
          bp.phrase_id === ph.id and
          bp.region_id === r.id and
          b.network_banner_id === network_banner_id and
          ph.network_phrase_id === network_phrase_id and
          r.network_region_id === network_region_id
        ) select(bp)).toList
  }
}


/**
* class is used for BannerPhrase creation
* received from Report Helpers
*/
@BeanInfo
case class BannerPhraseHelper(
  val network_banner_id: String,
  val network_phrase_id: String,
  val network_region_id: String,
  val bid: Double,
  val phrase: String = "" //TODO: retrieve from Report dictionary
)


/**
* class is used for BannerPhrase as Json creation
* send as current recommendations
*/
@BeanInfo
case class BannerPhraseJsonHelper(
  val bannerID: String,
  val phrase_id: String,
  val regionID: String,
  val bid: Double
)
