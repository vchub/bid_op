package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._

@BeanInfo
case class Region(
  var region_id: Long = 0, //official region id
  var parent_id: Long = 0, //fk to super Region
  val network_region_id: String = "", // Region ID in a Network's or client's DB
  val description: String = "") extends domain.Region with KeyedEntity[Long] {
  val id: Long = 0

  /**
   * select Parent Region
   */
  def parentRegion: Option[Region] = inTransaction {
    from(AppSchema.regions)((r) =>
      where(r.id === parent_id)
        select (r)).headOption
  }

  // Region -* BannerPhrase relation
  lazy val bannerPhrasesRel: OneToMany[BannerPhrase] = AppSchema.regionBannerPhrases.left(this)

  /**
   * default put - save to db
   */
  def put(): Region = inTransaction { AppSchema.regions insert this }

}

object Region {

  /**
   * get Region from DB
   */
  def get_by_id(id: Long): Region = inTransaction { AppSchema.regions.where(a => a.id === id).single }

  /**
   * construct Region from domain.Region
   */
  def apply(region: domain.Region): Region =
    Region(
      network_region_id = region.network_region_id,
      parent_id = region.parentRegion map (_.id) getOrElse (0))

  /**
   * select by Campaing and domain.Region (basically network_region_id)
   * TODO: now it's simply wrong. it has to check BP-B-Campaing association
   */
  def select(region: domain.Region): Option[Region] = inTransaction {
    AppSchema.regions.where(a =>
      a.network_region_id === region.network_region_id).headOption
  }
}
