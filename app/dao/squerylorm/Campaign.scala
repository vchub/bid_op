package dao.squerylorm

import org.squeryl.{Schema, KeyedEntity, Query}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.lang.RuntimeException
import java.util.Date
import org.joda.time._
import scala.reflect._
import common._


@BeanInfo
case class Campaign(
  var user_id: Long = 0, //fk
  var network_id: Long = 0, //fk
  val network_campaign_id: String = "", //campaign_id in the Network {Google, Yanddex, etc.) or User's DB
  val startDate: Date = new Date
  ) extends KeyedEntity[Long]
{
  val id: Long = 0


  // Campaign -* Banner relation
  lazy val banners: OneToMany[Banner] = AppSchema.campaignBanners.left(this)

  // Campaign -* Curve relation
  lazy val curves: OneToMany[Curve] = AppSchema.campaignCurves.left(this)

  // Campaign -* CampaignPerformance relation
  lazy val performances: OneToMany[CampaignPerformance] = AppSchema.campaignPerformance.left(this)

  // Campaign -* BudgetHistory relation
  lazy val budgetHistory: OneToMany[BudgetHistory] = AppSchema.campaignBudgetHistory.left(this)

  // Campaign -* EndDateHistory relation
  lazy val endDateHistory: OneToMany[EndDateHistory] = AppSchema.campaignEndDateHistory.left(this)


  /**
  * default put - save to db
  **/
  def put(): Campaign = inTransaction { AppSchema.campaigns insert this }

  /**
  * get list of all Banner for the Campaign
  */
  def get_banners(): List[Banner] = inTransaction {
    from(banners)(( b ) => where(b.campaign_id === id) select(b)).toList
  }

  /**
  * get list of BannerPhrase for the Campaign for all Regions
  */
  def get_bannerphrases(): List[BannerPhrase] = inTransaction {
    from(banners, AppSchema.bannerphrases)(( b, bp ) => where(b.id === id and
      b.id === bp.banner_id ) select(bp) ).toList
  }


  /**
  * get the latest Curve where Curve.start_date <= date
  * @throw Exception if param:Date is out of range i.e. too early
  */
  // TODO: Optimize for a view - 'limit = 1'
  def select_latest_curve(date: Date): List[Curve] = inTransaction {
    from(curves)((c) =>
      where(c.campaign_id === id
      and c.date <= date) select(c)
      orderBy(c.date desc) ).page(0, 1).toList
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
  def create(cc: domain.Campaign, daos: dao.Dao): domain.Campaign = inTransaction{
    // create DB Campaign
    val c = Campaign(
      user_id = cc.user.get.id,
      network_id = cc.network.get.id,
      network_campaign_id = cc.network_campaign_id,
      startDate = cc.startDate.toDate
    ).put
    // create BudgetHistory
    val budgetHistory = BudgetHistory(
      campaign_id = c.id,
      date = c.startDate,
      budget = cc.budget.get
    ).put
    // create EndDateHistory
    val endDateHistory = EndDateHistory(
      campaign_id = c.id,
      date = c.startDate,
      endDate = cc.endDate.get.toDate
    )

    //create domain.Campaign
    domain.Campaign(
      id = c.id,
      network_campaign_id = cc.network_campaign_id,
      startDate = cc.startDate,
      endDate = cc.endDate,
      budget = cc.budget,

      user = cc.user,
      network = cc.network,
      bannerPhrases = Nil,

      curves = Nil,
      performanceHistory = Nil,
      permutationHistory = Nil,

      // ? may be convert squerylorm.BudgetHistory to domain.BudgetHistory and fill ?
      budgetHistory = Nil,
      endDateHistory = Nil
    )

  }



  /** retrieves full domain model (Campaign and its Histories) for given Dates from DB
  * TODO: add Date filter!
  */
  def selectCampaignHistory(campaign_id: Long, startDate: DateTime, endDate: DateTime, daos: dao.Dao) = inTransaction{
    //select Campaign
    val c = get_by_id(campaign_id)
    //select Curves
    val s_curves = c.curves.toList
    val curves = s_curves map(_.domainCurve)
    //select Permutations
    val permutationHistory = s_curves map((c:Curve) =>
      c.permutations.toList map (_.domainPermutation)
    ) flatten


    //select BannerPhrases
    val bp = c.get_bannerphrases map(_.domainBannerPhrase)
    //select CampaignPerformance
    val campaignPerformance = c.performances.toList map(_.domainPerformance)
    //select BudgetHistory
    val budgetHistory = c.budgetHistory.toList map(_.domainTSValue)
    val budget = budgetHistory.lastOption map(_.elem)
    //select EndDateHistory and current endDate
    val endDateHistory = c.endDateHistory.toList map(_.domainTSValue)
    val endDate_from_endDayHistory = endDateHistory.lastOption map(_.elem)

    //create domain.Campaign
    val campaign = domain.Campaign(
      id = c.id,
      network_campaign_id = c.network_campaign_id,
      startDate = new DateTime(c.startDate),
      endDate = endDate_from_endDayHistory,
      budget = budget,

      user = None,
      network = None,
      bannerPhrases = bp,

      curves = curves,
      performanceHistory = campaignPerformance,
      permutationHistory = permutationHistory,

      budgetHistory = budgetHistory,
      endDateHistory = endDateHistory
    )

    // create CampaignHistory
    domain.CampaignHistory(campaign = campaign, startDate = startDate, endDate = endDate)
  }




  /** makes a "shallow" domain.Campaign - i.e. all aggregations are empty
  */
  def shallow_mapper(c: Campaign, daos: dao.Dao): domain.Campaign = new domain.Campaign(
    id = c.id,
    network_campaign_id = c.network_campaign_id,
    startDate = new DateTime(c.startDate),
    endDate = None, //c.endDateHistory.toList.last.elem, //TODO: Optimize
    budget = None,

    user = None,
    network = None,
    bannerPhrases = Nil, //List[Banner],

    curves = Nil, //List[Curve],
    performanceHistory = Nil, //List[Performance],
    permutationHistory = Nil, //List[Permutation],

    budgetHistory = Nil, //List[TSValue[Double]],
    endDateHistory = Nil //List[TSValue[DateTime]],
  )



}

