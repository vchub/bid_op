package dao.squschema

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._


@BeanInfo
case class Banner(
  val campaign_id: Long = 0, //fk
  val network_banner_id: String = "" // banner_id in Network's or client's DB
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // Campaign -* Banner relation
  lazy val campaign: ManyToOne[Campaign] = AppSchema.campaignBanners.right(this)

  // Banner -* BannerPhrase relation
  lazy val bannerPhrases: OneToMany[BannerPhrase] = AppSchema.bannerBannerPhrases.left(this)

  /**
  * default put - save to db
  * @param
  * @return Banner
  **/
  def put(): Banner = inTransaction { AppSchema.banners insert this }
}

object Banner {

  /**
  * select by network_banner_id
  * @param String
  * @return List[Banner]
  */
  def select(network_banner_id: String): List[Banner] = inTransaction{ AppSchema.banners.where(a => a.network_banner_id === network_banner_id).toList}//.single }
}
