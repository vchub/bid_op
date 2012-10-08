package domain

import org.squeryl.{Schema, KeyedEntity, Query}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.lang.RuntimeException
import java.util.Date
import java.text.SimpleDateFormat
import org.joda.time._
import scala.reflect._
import common._
import domain.dao.AppSchema
import domain.dao.AppSchemaFactory
import domain.serializing.helpers

@BeanInfo
case class Campaign(
  val network_campaign_id: String = "", //campaign_id in the Network {Google, Yanddex, etc.) or User's DB
  val start_date: Date = new Date,
  val end_date: Date = new Date ,
  val budget: Double = 0
  ) extends KeyedEntity[Long]
{
  @transient
  val id: Long = 0
  @transient
  var user_id: Long = 0 //fk
  @transient
  var network_id: Long = 0 //fk



  // initialize factory with object AppSchemaFactory
  // factory is used to make Dependency Injection (DI) possible
  @transient
  var app_schema_factory = new AppSchemaFactory


  // Campaign -* Banner relation
  @transient
  lazy val banners: OneToMany[Banner] = AppSchema.campaignBanners.left(this)

  // Campaign -* Curve relation
  @transient
  lazy val curves: OneToMany[Curve] = AppSchema.campaignCurves.left(this)

  // Campaign -* Curve relation
  @transient
  lazy val recommendations: OneToMany[Recommendation] = AppSchema.campaignRecommendations.left(this)




  /**
  * TODO: well I guess it's gonna be Controller's function...
  * - Constructor.
  * - Constructor has to create default Curve
  * - Check out what else Constructor has to create
  **/


  /**
  * default put - save to db
  * @param
  * @return Campaign
  **/
  def put(): Campaign = inTransaction { AppSchema.campaigns insert this }

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
  def get_bannerphrases(): List[BannerPhrase] = inTransaction {
    from(banners, AppSchema.bannerphrases)(( b, bp ) => where(b.id === id and
      b.id === bp.banner_id ) select(bp) ).toList
  }

  /**
  * get list of BannerPhrase for the Campaign and Region
  * @param
  * @return List[BannerPhrase]
  */
  // TODO:
  def get_bannerphrases(region: Region): List[BannerPhrase] = ???

  /**
  * check out whether recommendations are renewed since a date
  * func is used for fast response to current recommendation status request
  * @param
  * @return Boolean
  */
  def recommendations_changed_since(date: Date): Boolean = inTransaction {
    val q = from(recommendations)((r) => where(r.campaign_id === id
      and r.start_date > date )
      select(r)
      orderBy(r.start_date asc) ).page(0, 1) // TODO: add index!
      if( q.isEmpty ) false else true
  }


  /**
  * select the latest BannerPhrase and Permutation (s)
  * @param
  * @return Query[BannerPhrase, Permutation]
  */
  def select_BannerPhrases_Permutations(date: Date) = inTransaction {
    // select the latest curve
    val latest_curves = from(curves)((c) =>
      where(c.campaign_id === id
      and c.start_date <= date) select(c)
      orderBy(c.start_date desc) ).page(0, 1)

    // get the latest Timeslot for the given date
    from(AppSchema.bannerphrases, AppSchema.permutations, AppSchema.timeslots)((b, p, t) =>
      where(
        t.curve_id in from(latest_curves)((c)=>select(c.id)) and
        b.id === p.bannerphrase_id and
        p.timeslot_id === t.id
      )
      select((b,p))
    )
  }

  /**
  * select current (the latest) recommendations
  * @param
  * @return List[BannerPhraseJsonHelper]
  */
  def select_current_recommedations(date: Date) = inTransaction {
    // get all BannerPhrase and Permutation(s) for the latest Timeslot
    // and create BannerPhraseHelper
    val query = select_BannerPhrases_Permutations(date)
    query map { bpp =>
      val (bp, p) = (bpp._1, bpp._2)
      val network_banner_id = bp.banner.head.network_banner_id
      val network_phrase_id = bp.phrase.head.network_phrase_id
      val network_region_id = bp.region.head.network_region_id
      val bid = p.id
      BannerPhraseJsonHelper(network_banner_id, network_phrase_id, network_region_id, bid)
    }
  }



  /**
  * get List[Curve] from start_date till end_date
  * @param (start_date, end_date)
  * @return List[Curve]
  */
  // TODO: Add index to db
  def get_curves(start_date: Date, end_date: Date): List[Curve] = inTransaction {
    from(AppSchema.campaigns, curves)((cam, c) => where(c.campaign_id === id and cam.id === id
      and c.start_date >= start_date and c.start_date <= end_date)
        select(c) orderBy(c.start_date asc) ).toList
  }

  /**
  * get List[(Curve, Timeslot)] from start_date till end_date
  * orderBy Timeslot "start_date"
  * @param (start_date, end_date)
  * @return List[(Curve, Timeslot)]
  * TODO: probably should be changed to List((Curve, List[TimeSlot]))
  */
  // TODO: Add index to db
  def get_curves_timeslots(start_date: Date, end_date: Date): List[(Curve, TimeSlot)] = inTransaction {
    // print query
    // org.squeryl.Session.currentSession.setLogger(println(_))

    from(AppSchema.campaigns, AppSchema.curves, AppSchema.timeslots)((cam, c, t) => where(c.campaign_id === id and cam.id === id
      and c.id === t.curve_id
      and c.start_date >= start_date and c.start_date <= end_date)
      select (c, t) orderBy(c.start_date, t.start_date asc) ).toList
  }

  /**
  * get List[(Curve, Timeslot, Permutation)] from start_date till end_date
  * orderBy Timeslot "start_date"
  * @param (start_date, end_date)
  * @return List[(Curve, Timeslot)]
  * TODO: probably should be changed to List[ (Curve, List(TimeSlot, List[Permutation])) ]
  */
  // TODO: Add index to db
  def get_curves_timeslots_permutations(start_date: Date, end_date: Date)
    : List[(Curve, TimeSlot, Permutation)] = inTransaction {
    // print query
    // org.squeryl.Session.currentSession.setLogger(println(_))

    from(AppSchema.campaigns, AppSchema.curves, AppSchema.timeslots, AppSchema.permutations)((cam, c, t, p) =>
      where(cam.id === id and c.campaign_id === id and c.id === t.curve_id
      and p.timeslot_id === t.id
      and c.start_date >= start_date and c.start_date <= end_date)
      select (c, t, p) orderBy(c.start_date, t.start_date asc) ).toList
  }


  /**
  * put new CampaignStats and TimeSlot
  * TimeSlot.curve_id refers to the newest Curve
  * @param (CampaignStats, start_date, end_date)
  * @return CampaignStats
  */
  // TODO: Optimize for one DB trip ?
  /*
  def put_campaignstats(campaignstat : CampaignStats, start_date: Date, end_date: Date)
    :CampaignStats = inTransaction {
    // get the latest Curve
    val curve = get_latest_curve(start_date)

    // create new TimeSlot
    // notice that TimeSlotType object is used in a way to let DI be possible
    val timeslottype = (app_schema_factory.get_TimeSlotType()).get_timeslottype(start_date, end_date)

    val timeslot = (new TimeSlot(curve.id, timeslottype.id, start_date, end_date)) put

    // put TimeSlot and CampaignStats
    campaignstat.timeslot_id = timeslot.id
    campaignstat put
  }
  */

  /**
  * get the latest Curve where Curve.start_date <= date
  * @param Date
  * @return Query[Curve]
  * @throw Exception if param:Date is out of range i.e. too early
  */
  // TODO: Optimize for a view - 'limit = 1'
  def select_latest_curve(date: Date): List[Curve] = inTransaction {
    from(curves)((c) =>
      where(c.campaign_id === id
      and c.start_date <= date) select(c)
      orderBy(c.start_date desc) ).page(0, 1).toList
  }

  /**
  * insert new Timeslot for the appropriate Curve and TimeSlotType
  * @param Timeslot
  * @return Timeslot
  * @throw Exception if there's no Curve for the Timeslot.start_date
  * i.e. the earliest Curve.start_date is after Timeslot.start_date
  */
  // TODO: Optimize for a view - 'limit = 1'
  def insert(ts: TimeSlot): TimeSlot = inTransaction {
    //find the latest Curve
    val curve = select_latest_curve(ts.start_date)
    if(curve.isEmpty) throw new RuntimeException("No Curve for the date:" + start_date)
    else {
      // find out timeslottype
      val timeslottype = (app_schema_factory.get_TimeSlotType()).get_timeslottype(start_date, end_date)
      ts.timeslottype_id = timeslottype.id
      ts.curve_id = curve.head.id
      ts.put
    }
  }

  /**
  * process Report containing detailed BannerPhrase performance
  * creates new Banners, Phrases, Regions and BannerPhrase if needed
  * @param xml.NodeSeq - xml Report
  * @return Boolean - success - Report processed, entities created
  * @throw java.lang.RuntimeException - if Report is malformed - nothing created
  */
  def process_report(node: scala.xml.NodeSeq, reportHelper: helpers.ReportHelper): Boolean = {
    // create List[(BannerPhraseHelper, BannerPhraseStats)]
    val bp_st = reportHelper.createBannerPhraseStats(node)

    inTransaction {
      val res = bp_st map {entry =>
        val (bp, stats) = (entry._1, entry._2)
        // find if BannerPhrase already exists
        BannerPhrase.select(this, bp.network_banner_id, bp.network_phrase_id,
              bp.network_region_id) match {

          case bannerphrase::Nil => { // put stats into DB
              // associate (and put) BannerPhraseStats
              val bannerPhraseStats = bannerphrase.bannerPhraseStats.associate(stats)
            }

          case Nil => { // check out what's absent (Banner, Phrase, Region), create it
                          // and put created and stats into DB
              val banner = Banner.select(bp.network_banner_id).headOption.getOrElse {
                this.banners.associate(Banner(0,bp.network_banner_id))
              }
              val phrase = Phrase.select(bp.network_phrase_id).headOption.getOrElse {
                //TODO: add phrase - have to be done in ReportHelper (dictionary)
                (Phrase(bp.network_phrase_id, bp.phrase)).put
              }
              val region = Region.select(bp.network_region_id).headOption.getOrElse {
                (Region(0, 0, bp.network_region_id, "")).put
              }
              // create and put BannerPhrase
              val bannerphrase = BannerPhrase(banner_id = banner.id,
                phrase_id = phrase.id, region_id = region.id).put
              // associate (and put) BannerPhraseStats
              val bannerPhraseStats = bannerphrase.bannerPhraseStats.associate(stats)
            }

          case _ => { // what the heck? we can't have more than one BannerPhrase
                throw new RuntimeException("""DB conatains more than one BannerPhrase with
                  identical network_banner_id: %s, network_phrase_id: %s and network_region_id: %s""".
                  format(bp.network_banner_id, bp.network_phrase_id, bp.network_region_id))
            }
        }
      }
      res.length match {
        case 0 => false // nothing created
        case _ => true  // Something created and no Exceptions thrown
      }
    }
  }



}

object Campaign {

  /**
  * get Campaign from DB
  * @return Campaign
  **/
  def get_by_id(id: Long): Campaign = inTransaction{ AppSchema.campaigns.where(a => a.id === id).single }


  /**
  * select Campaigns for given user_name, network_name and network_campaign_id
  * it should be 1 Campaign generally
  * @param user_name:String, network_name:String, network_campaign_id: String
  * @return Campaign
  **/
  def select(user_name: String, network_name: String, network_campaign_id: String ): List[Campaign] = inTransaction{
      from(AppSchema.campaigns, AppSchema.users, AppSchema.networks)((c, u, n) =>
        where(c.network_campaign_id === network_campaign_id
        and u.name === user_name and n.name === network_name
        and c.user_id === u.id and c.network_id === n.id)
        select(c)).toList
  }

  /**
  * select Campaigns for given User, Network and network_campaign_id
  * it should be 1 Campaign generally
  * @param User, Network, network_campaign_id: String
  * @return Campaign
  **/
  def select(user: User, network: Network, network_campaign_id: String ): List[Campaign] = inTransaction{
      from(AppSchema.campaigns)((c) =>
        where(c.network_campaign_id === network_campaign_id
        and c.user_id === user.id and c.network_id === network.id)
        select(c)).toList
  }

  /**
  * select all campaigns of User and Network
  * @param User, Network
  * @return List[Campaign]
  **/
  def select(user: User, network: Network): List[Campaign] = inTransaction {
    from(AppSchema.campaigns)((c) =>
      where(c.user_id === user.id
      and c.network_id === network.id)
      select(c)).toList
  }

  /**
  * select all campaigns of User
  * @param
  * @return List[Campaign]
  **/
  def select(user: User): List[Campaign] = inTransaction {
    from(AppSchema.campaigns)((c) => where(c.user_id === user.id ) select(c)).toList
  }

}

