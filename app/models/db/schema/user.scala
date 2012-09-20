package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._

import scala.reflect._

import AppSchema._
import common._


@BeanInfo
case class User(
  val name: String = ""
  )
  {
    /**
    * default put - save to db
    * @param
    * @return Campaign
    */
    def put(): User = inTransaction { users insert this }

    /**
    * get all campaigns of User and Network
    * @param Network name
    * @return List[Campaign]
    */
    def get_all_campaigns(network_name: String) = inTransaction {
      from(campaigns)((c) => where(c.user_name === name and c.network_name === network_name)
        select(c)).toList
    }

    /**
    * get particula campaign for User and Network and network_campaign_id
    * @param Network name: String, network_campaign_id: String
    * @return Campaign
    */
    def get_campaign_for_net_and_net_id(network_name: String, network_campaign_id: String) = inTransaction {
        from(campaigns)(( c ) => where(c.user_name === name and c.network_name === network_name
        and c.network_campaign_id === network_campaign_id) select(c)).toList
      }
  }
object User {
  def get_by_name(name: String): User = inTransaction{ users.where(a => a.name === name).single }
}


