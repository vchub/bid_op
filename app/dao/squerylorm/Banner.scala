package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._


@BeanInfo
case class Banner(
  val network_banner_id: String = "" // banner_id in Network's or client's DB
)extends domain.Banner with KeyedEntity[Long]
{
  val id: Long = 0


  // Banner -* BannerPhrase relation
  lazy val bannerPhrasesRel: OneToMany[BannerPhrase] = AppSchema.bannerBannerPhrases.left(this)

  /**
  * default put - save to db
  */
  def put(): Banner = inTransaction { AppSchema.banners insert this }



}

object Banner {

  /** construct Banner from domain.Banner
  */
  def apply(b: domain.Banner): Banner =
    Banner(
      network_banner_id = b.network_banner_id
    )


  /**
  * select by Campaing and domain.Banner (basically network_banner_id)
  * TODO: now it's simply wrong. it has to check BP-B-Campaing association
  */
  def select(b: domain.Banner): Option[Banner] = inTransaction{
    AppSchema.banners.where(a =>
      a.network_banner_id === b.network_banner_id
    ).headOption
  }


}
