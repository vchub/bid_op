package models.db
package schema

import org.squeryl.{Session, SessionFactory}
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.annotations.Column
import java.util.Date
import scala.reflect._



object AppSchema extends Schema {
  //When the table name doesn't match the class name, it is specified here :
  //val authors = table[Author]("AUTHORS"),
  val campaigns = table[Campaign]
  on(campaigns)(s => declare(
    //s.id is (primaryKey,autoIncremented)
  ))

  val users = table[User]("ad_user")
  on(users)(s => declare(
    s.name is (primaryKey, unique)
  ))

  val networks = table[Network]
  on(networks)(s => declare(
    s.name is (primaryKey, unique)
  ))

  val banners = table[Banner]
  val phrases = table[Phrase]
  val bannerphrases = table[BannerPhrase]








  /*
  val phrases = table[Phrase]
  val campaign_stats = table[CampaignStats]

  on(campaigns)(s => declare(
    //s.id is (autoIncremented, unique)
  ))

  on(phrases)(s => declare(
    s.campaign_id is(indexed)
  ))

  on(campaign_stats)(s => declare(
    //s.id is (autoIncremented, unique)
  ))
  */
  override def drop = {
    Session.cleanupResources
    super.drop
  }

}
