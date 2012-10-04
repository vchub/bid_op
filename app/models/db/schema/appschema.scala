package models.db
package schema

import org.squeryl.{Session, SessionFactory}
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.annotations.Column
import org.squeryl.dsl._

import java.util.Date
import scala.reflect._




object AppSchema extends Schema {
  //When the table name doesn't match the class name, it is specified here :
  //val authors = table[Author]("AUTHORS"),


  val banners = table[Banner]

  val bannerphrases = table[BannerPhrase]

  val bannerphrasestats = table[BannerPhraseStats]



  //val campaignstats = table[CampaignStats]

  val curves = table[Curve]
  on(curves)(c => declare(
    c.start_date is(dbType("timestamp"))  //Note: timestamp is valid type for Postgres and H2
                                          // while for Mysql it's datetime
  ))

  val networks = table[Network]
  on(networks)(s => declare(
    s.name is (unique)
  ))

  val permutations = table[Permutation]

  val phrases = table[Phrase]

  val recommendations = table[Recommendation]
  on(recommendations)(r => declare(
    r.start_date is(dbType("timestamp"))  //Note: timestamp is valid type for Postgres and H2
  ))

  val regions = table[Region]

  val timeslots = table[TimeSlot]
  on(timeslots)(c => declare(
    c.start_date is(dbType("timestamp")),  //Note: timestamp is valid type for Postgres and H2
    c.end_date is(dbType("timestamp"))    // while for Mysql it's datetime
  ))

  val timeslottypes = table[TimeSlotType]

  val users = table[User]("ad_user")
  on(users)(s => declare(
    s.name is (unique)
  ))



 //User *-* Network relation
 val campaigns = manyToManyRelation(users, networks).
    via[Campaign]((u,n,c) => (c.user_id === u.id, n.id === c.network_id))
  on(campaigns)(c => declare(
    c.network_campaign_id is(unique)
  ))

  // Campaign -* Banner relation
  val campaignBanners = oneToManyRelation(campaigns, banners).via((c,b) => c.id === b.campaign_id)

  // Campaign -* Curve relation
  val campaignCurves = oneToManyRelation(campaigns, curves).via((c,b) => c.id === b.campaign_id)

  // Campaign -* Recommendation relation
  val campaignRecommendations = oneToManyRelation(campaigns, recommendations).via((c,b) => c.id === b.campaign_id)

  // Banner -* BannerPhrase relation
  val bannerBannerPhrases = oneToManyRelation(banners, bannerphrases).via((b, bp) => b.id === bp.banner_id)

  // Phrase -* BannerPhrase relation
  val phraseBannerPhrases = oneToManyRelation(phrases, bannerphrases).via((b, bp) => b.id === bp.phrase_id)

  // Region -* BannerPhrase relation
  val regionBannerPhrases = oneToManyRelation(regions, bannerphrases).via((b, bp) => b.id === bp.region_id)

  // BannerPhrase -* BannerPhraseStats relation
  val bannerPhraseBannerPhraseStats = oneToManyRelation(bannerphrases, bannerphrasestats)
    .via((b, bp) => b.id === bp.bannerphrase_id)

  // BannerPhrase -* Permutation relation
  val bannerPhrasePermutatons = oneToManyRelation(bannerphrases, permutations)
    .via((b, bp) => b.id === bp.bannerphrase_id)

  // Curve -* TimeSlot relation
  val curveTimeSlots = oneToManyRelation(curves, timeslots)
    .via((b, bp) => b.id === bp.curve_id)

  // TimeSlotType -* TimeSlot relation
  val timeSlotTypeTimeSlots = oneToManyRelation(timeslottypes, timeslots)
    .via((b, bp) => b.id === bp.timeslottype_id)


  // TODO: it seems that CampaignStats and TimeSlot have 1-1 relation - unite them
  // TimeSlot -* CampaignStats relation
  /*
  val timeSlotCampaignStats = oneToManyRelation(timeslots, campaignstats)
    .via((b, bp) => b.id === bp.timeslot_id)
    */

  // TimeSlot -* Permutation relation
  val timeSlotPermutation = oneToManyRelation(timeslots, permutations)
    .via((b, bp) => b.id === bp.timeslot_id)




  override def drop = {
    Session.cleanupResources
    super.drop
  }

}

