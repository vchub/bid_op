package models.db
package schema

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import scala.reflect._




@BeanInfo
case class Region(
  var region_id: Long = 0, //official region id
  var parent_id: Long = 0, //fk to super Region
  val network_region_id: String = "", // Region ID in a Network's or client's DB
  val description: String = ""
)extends KeyedEntity[Long]
{
  val id: Long = 0


  // Region -* BannerPhrase relation
  lazy val bannerPhrases: OneToMany[BannerPhrase] = AppSchema.regionBannerPhrases.left(this)


  /**
  * default put - save to db
  * @param
  * @return Region
  **/
  def put(): Region = inTransaction { AppSchema.regions insert this }
}

object Region {

  /**
  * select by network_region_id
  * @param String
  * @return List[Region]
  */
  def select(network_region_id: String): List[Region] = inTransaction{ AppSchema.regions.where(a => a.network_region_id === network_region_id).toList}//.single }
}
