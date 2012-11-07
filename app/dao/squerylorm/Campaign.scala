package dao.squerylorm

import org.squeryl.{Schema, KeyedEntity, Query}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.squeryl.annotations.Transient
import java.lang.RuntimeException
import org.joda.time._
import java.sql.Timestamp
import scala.reflect._
import common._


@BeanInfo
case class Campaign(
  var user_id: Long = 0, //fk
  var network_id: Long = 0, //fk
  val network_campaign_id: String = "", //campaign_id in the Network {Google, Yanddex, etc.) or User's DB
  val start: Timestamp = new Timestamp(1)
) extends domain.Campaign with KeyedEntity[Long]
{
  val id: Long = 0
  def startDate = start

  @Transient
  var historyStartDate: DateTime = new DateTime
  @Transient
  var historyEndDate: DateTime = new DateTime


  /*
  // Campaign -* Banner relation
  lazy val bannersRel: OneToMany[Banner] = AppSchema.campaignBanners.left(this)
  */

  // Campaign -* BannerPhrase relation
  lazy val bannerPhrasesRel: OneToMany[BannerPhrase] = AppSchema.campaignBannerPhrases.left(this)

  // Campaign -* Curve relation
  lazy val curvesRel: OneToMany[Curve] = AppSchema.campaignCurves.left(this)

  // Campaign -* Permutation relation
  lazy val permutationsRel: OneToMany[Permutation] = AppSchema.campaignPermutations.left(this)

  // Campaign -* CampaignPerformance relation
  lazy val performancesRel: OneToMany[CampaignPerformance] = AppSchema.campaignPerformance.left(this)

  // Campaign -* BudgetHistory relation
  lazy val budgetHistoryRel: OneToMany[BudgetHistory] = AppSchema.campaignBudgetHistory.left(this)

  // Campaign -* EndDateHistory relation
  lazy val endDateHistoryRel: OneToMany[EndDateHistory] = AppSchema.campaignEndDateHistory.left(this)

  // Campaign History in ascending order and in conformance to campaign.historyStartDate, historyEndDate
  def getHistory[T<:History](qRel: Query[T]): List[T] = if(historyStartDate != historyEndDate) inTransaction {
      from(qRel)((b) =>
        where(b.date >= convertToJdbc(historyStartDate)
          and b.date <= convertToJdbc(historyEndDate))
        select(b) orderBy(b.date asc)).toList
    }
    else Nil


  lazy val endDateHistory = getHistory[EndDateHistory](endDateHistoryRel)
  def endDate: Option[DateTime] = endDateHistory.lastOption map( (x:EndDateHistory) => x.endDate)

  lazy val budgetHistory = getHistory[BudgetHistory](budgetHistoryRel)
  def budget = budgetHistory.lastOption map (_.budget)

  def user = None
  def network = None

  //get_bannerphrases and assign Campaign (with historyStartDate, historyEndDate)
  lazy val bannerPhrases: List[domain.BannerPhrase] = inTransaction{ bannerPhrasesRel.toList map (
    x => {x.campaign = Some(this); x})
  }

  lazy val curves = getHistory[Curve](curvesRel)
  lazy val performanceHistory = getHistory[CampaignPerformance](performancesRel)
  lazy val permutationHistory = getHistory[Permutation](permutationsRel)





  /**
  * default put - save to db
  **/
  def put(): Campaign = inTransaction { AppSchema.campaigns insert this }

  /**
  * get list of all Banner for the Campaign
  def get_banners(): List[Banner] = inTransaction {
    from(bannersRel)(( b ) => where(b.campaign_id === id) select(b)).toList
  }

  * get list of BannerPhrase for the Campaign for all Regions
  def get_bannerphrases(): List[BannerPhrase] = inTransaction {
    from(bannersRel, AppSchema.bannerphrases)(( b, bp ) => where(b.id === id and
      b.id === bp.banner_id ) select(bp) ).toList
  }
  */


  /**
  * get the latest Curve where Curve.start_date <= date
  * @throw Exception if param:Date is out of range i.e. too early
  // TODO: Optimize for a view - 'limit = 1'
  def select_latest_curve(date: Date): List[Curve] = inTransaction {
    from(curvesRel)((c) =>
      where(c.campaign_id === id
      and c.date <= date) select(c)
      orderBy(c.date desc) ).page(0, 1).toList
  }
  */



  /** creates BannerPhrasePerformance records
  * creates new Banners, Phrases, Regions and BannerPhrase if needed
  * @throw java.lang.RuntimeException - if Report is malformed - nothing created
  */
  def create(report: Map[domain.BannerPhrase, domain.Performance]): Boolean = inTransaction{
    val res = report map {case (bp, performance) =>
      // find if BannerPhrase already exists
      for ( b <- bp.banner; p <- bp.phrase; r <- bp.region) yield {
        BannerPhrase.select(this, b.network_banner_id, p.network_phrase_id, r.network_region_id) match {

        case bannerphrase::Nil => { // put Performance into DB
            // associate (and put) BannerPhraseStats
            bannerphrase.bannerPhrasePerformanceRel.associate(BannerPhrasePerformance((bp, performance)))
          }

        case Nil => { // check out what's absent (Banner, Phrase, Region), create it
                        // and put created and stats into DB
            val banner = Banner.select(b).headOption.getOrElse {
              // create new Banner in DB
              (Banner(b)).put
            }
            val phrase = Phrase.select(p).headOption.getOrElse {
              //TODO: add phrase - have to be done in ReportHelper (dictionary)
              // create new Phrase in DB
              (Phrase(p)).put
            }
            val region = Region.select(r).headOption.getOrElse {
              // create new Region in DB
              (Region(r)).put
            }
            require(phrase.id != 0)
            require(region.id != 0)
            require(banner.id != 0)
            // create and put BannerPhrase
            val bannerphrase = BannerPhrase(campaign_id = this.id, banner_id = banner.id,
              phrase_id = phrase.id, region_id = region.id).put
            //check bp
            require(bannerphrase.id != 0)
            // associate (and put) BannerPhrasePerformance
            //bannerphrase.bannerPhrasePerformanceRel.associate(BannerPhrasePerformance((bp, performance)))
            val bp_perf = BannerPhrasePerformance((bannerphrase, performance)).put
            require(bp_perf.id != 0)
            require(bp_perf.bannerphrase_id == bannerphrase.id)
          }

        case _ => { // what the heck? we can't have more than one BannerPhrase
              throw new RuntimeException("""DB conatains more than one BannerPhrase with
                identical network_banner_id: %s, network_phrase_id: %s and network_region_id: %s""".
                format(b.network_banner_id, p.network_phrase_id, r.network_region_id))
          }
      }
    }
    }
    res.nonEmpty
  }


}

object Campaign {

  /** get Campaign from DB
  */
  def get_by_id(id: Long): Campaign = inTransaction{ AppSchema.campaigns.where(a => a.id === id).single }


  /** select Campaigns for given user_name, network_name and network_campaign_id
  * it should be 1 Campaign generally
  */
  def select(user_name: String, network_name: String, network_campaign_id: String ): List[Campaign] = inTransaction{
      from(AppSchema.campaigns, AppSchema.users, AppSchema.networks)((c, u, n) =>
        where(c.network_campaign_id === network_campaign_id
        and u.name === user_name and n.name === network_name
        and c.user_id === u.id and c.network_id === n.id)
        select(c)).toList
  }

  /**
  * select Campaigns for given user_name, network_name
  **/
  def select(user_name: String, network_name: String): List[Campaign] = inTransaction{
      from(AppSchema.campaigns, AppSchema.users, AppSchema.networks)((c, u, n) =>
        where(
        u.name === user_name and n.name === network_name
        and c.user_id === u.id and c.network_id === n.id)
        select(c)).toList
  }


  /**
  * select all campaigns of User
  **/
  def select(user: User): List[Campaign] = inTransaction {
    from(AppSchema.campaigns)((c) => where(c.user_id === user.id ) select(c)).toList
  }

  /** creates Campaign record
  */
  def create(cc: domain.Campaign): domain.Campaign = inTransaction{
    // create DB Campaign
    val c = Campaign(
      user_id = cc.user.get.id,
      network_id = cc.network.get.id,
      network_campaign_id = cc.network_campaign_id,
      start = cc.startDate
    ).put
    // create BudgetHistory
    val budgetHistory = BudgetHistory(
      campaign_id = c.id,
      date = c.start,
      budget = cc.budget.get
    ).put
    // create EndDateHistory
    val endDateHistory = EndDateHistory(
      campaign_id = c.id,
      date = c.start,
      endDate = cc.endDate.get
    ).put
    // return DB Campaign
    c
  }



  /** retrieves full domain model (Campaign and its Histories) for given Dates from DB
  * TODO: add Date filter!
  def selectCampaignWithHistory(campaign_id: Long, historyStartDate: DateTime, historyEndDate: DateTime) = inTransaction{ //, daos: dao.Dao) = inTransaction{
    //select Campaign
    val campaign = get_by_id(campaign_id)
    // set Histories Dates
    campaaign.historyStartDate = historyStartDate
    campaaign.historyEndDate = historyEndDate
    campaaign
  }
  */



}

