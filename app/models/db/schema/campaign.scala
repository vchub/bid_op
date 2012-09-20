package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._

import scala.reflect._

import AppSchema._
import common._


//@JsonIgnoreProperties(Array("name"))
@BeanInfo
case class Campaign(
  val user_name: String = "",   //fk
  val network_name: String = "", //fk
  val network_campaign_id: String = "" //campaign_id in the Network {Google, Yanddex, etc.) or User's DB
  ) extends KeyedEntity[Long]
  {
    val id: Long = 0

    /**
    * default put - save to db
    * @param
    * @return Campaign
    */
    def put(): Campaign = inTransaction { campaigns insert this }

    /**
    * get list of all BannerPhrase for the Campaign
    * @param
    * @return List[BannerPhrase]
    */
    def get_banners(): List[Banner] = inTransaction {
      from(banners)(( b ) => where(b.campaign_id === id) select(b)).toList
    }

    /**
    * get list of BannerPhrase for the Campaign for all Regions
    * @param
    * @return List[BannerPhrase]
    */
    def get_banner_phrases(): List[BannerPhrase] = inTransaction {
      from(banners, bannerphrases)(( b, bp ) => where(b.id === id and
        b.id === bp.banner_id ) select(bp) ).toList
    }

    /**
    * get list of BannerPhrase for the Campaign and Region
    * @param
    * @return List[BannerPhrase]
    */
    // TODO:
    def get_banner_phrases(region: Region): List[BannerPhrase] = ???
  }

object Campaign {
  def get_by_id(id: Long): Campaign = inTransaction{ campaigns.where(a => a.id === id).single }
}

