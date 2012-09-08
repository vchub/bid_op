package models.db.schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import scala.reflect._

// fields can be mutable or immutable - val or var
@BeanInfo
class Campaign(
     val user_id: Long,
     val network_campaign_id: Long, //campaign_id in the Network {Google, Yanddex, etc.) or User's DB
     val region_id: Long,
     val network: String,      //Google, Yanddex, etc
     val name: String
  ) extends KeyedEntity[Long]
  {
     val id: Long = 0
    def this() = this(0,0,0,"","")

    import AppDB._
    def put(): Campaign = inTransaction{ campaigns insert this }
    def get_by_id(id: Long): Campaign = inTransaction{ campaigns.where(a => a.id === id).single }
  }

@BeanInfo
case class Phrase(
     val campaign_id: Long,
     val region_id: Long,
     val phrase: String
  )extends KeyedEntity[Long]
  {
     val id: Long = 0
    def this() = this(0,0,"")
  }

@BeanInfo
case class CampaignStats(
     val campaign_id: Long,
     val clicks: Int,
     val impressions: Int
    //add Timestamps
  ) extends KeyedEntity[Long]
  {
     val id: Long = 0
    def this() = this(0,0,0)

    import AppDB.campaign_stats
    def put(): CampaignStats = inTransaction{ campaign_stats insert this }
  }



object AppDB extends Schema {
  //When the table name doesn't match the class name, it is specified here :
  //val authors = table[Author]("AUTHORS")
  val campaigns = table[Campaign]
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

}
