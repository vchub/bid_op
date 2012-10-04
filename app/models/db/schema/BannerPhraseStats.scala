package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import java.util.Date
import scala.reflect._

import common._


@BeanInfo
case class BannerPhraseStats(
  val bannerphrase_id: Long = 0, //fk
  val sum_search: Double = 0,
  val sum_context: Double = 0,
  val impress_search: Int = 0,
  val impress_context: Int = 0,
  val clicks_search: Int = 0,
  val clicks_context: Int = 0,
  val start_date: Date = new Date(), //Note: we'll have to select timeslots in date range
  val end_date: Date = new Date()    //
) extends KeyedEntity[Long]
{
  @transient
  val id: Long = 0

  // BannerPhrase -* BannerPhraseStats relation
  lazy val bannerPhrase: ManyToOne[BannerPhrase] = AppSchema.bannerPhraseBannerPhraseStats.right(this)


  /**
  * default put - save to db
  * @param
  * @return BannerPhraseStats
  **/
  def put(): BannerPhraseStats = inTransaction { AppSchema.bannerphrasestats insert this }
}

object BannerPhraseStats {

  /**
  * get BannerPhraseStats from DB
  * @return TimeSlot
  */
  def get_by_id(id: Long): BannerPhraseStats = inTransaction{
    AppSchema.bannerphrasestats.where(a => a.id === id).single }


  /**
  * parse json:String to List[BannerPhraseStats]
  * @return List[BannerPhraseStats]
  */

  def parse_and_insert(json_string: String, campaign_id: Long): List[BannerPhraseStats] = {
    import com.codahale.jerkson.Json

    val c = Json.parse[BannerPhraseStats](json_string)
    //TODO:
    val res: List[BannerPhraseStats] = List()
    res

    /*
    TimeSlot(start_date = c.start_date, end_date = c.end_date, sum_search = c.sum_search,
      sum_context = c.sum_context, impress_search = c.impress_search, impress_context = c.impress_search,
      clicks_search = c.clicks_search, clicks_context = c.clicks_context
    )
    */

  }
}


