package dao.squerylorm
import org.squeryl.Session
import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._

object AppSchema extends Schema {

  val users = table[User]("ad_user")
  on(users)(s => declare(
    s.name is (unique)))

  val networks = table[Network]
  on(networks)(s => declare(
    s.name is (unique)))

  val banners = table[Banner]
  val regions = table[Region]
  val phrases = table[Phrase]
  val bannerphrases = table[BannerPhrase]
  val positions = table[Position]
  val periodtypes = table[PeriodType]
  val recommendationchangedate = table[RecommendationChangeDate]

  //for test purpose
  val checktimes = table[CheckTime]
  on(checktimes)(c => declare(
    c.dateDate is (dbType("timestamp")) //Note: timestamp is valid type for Postgres and H2
    // while for Mysql it's datetime
    ))
  // TODO: change to timestamp w/ TimeZone
  val curves = table[Curve]
  on(curves)(c => declare(
    c.date is (dbType("timestamp")) //Note: timestamp is valid type for Postgres and H2
    // while for Mysql it's datetime
    ))

  val campaignperformance = table[CampaignPerformance]
  on(campaignperformance)(c => declare(
    c.date is (dbType("timestamp"))))

  val bannerphraseperformance = table[BannerPhrasePerformance]
  on(bannerphraseperformance)(c => declare(
    c.date is (dbType("timestamp"))))

  val permutations = table[Permutation]
  on(permutations)(c => declare(
    c.date is (dbType("timestamp"))))

  val actualbidhistory = table[ActualBidHistory]

  val recommendationhistory = table[RecommendationHistory]
  on(recommendationhistory)(c => declare(
    c.date is (dbType("timestamp"))))

  val netadvisedbidhistory = table[NetAdvisedBidHistory]
  on(netadvisedbidhistory)(c => declare(
    c.date is (dbType("timestamp"))))

  val budgethistory = table[BudgetHistory]
  on(budgethistory)(c => declare(
    c.date is (dbType("timestamp"))))

  val enddatehistory = table[EndDateHistory]
  on(enddatehistory)(c => declare(
    c.date is (dbType("timestamp"))))

  //User *-* Network relation
  val campaigns = manyToManyRelation(users, networks).
    via[Campaign]((u, n, c) => (c.user_id === u.id, n.id === c.network_id))
  on(campaigns)(c => declare(
    c.network_campaign_id is (unique)))

  // Campaign -* Curve relation
  val campaignCurves = oneToManyRelation(campaigns, curves).via((c, b) => c.id === b.campaign_id)
  // Campaign -* CampaignPerformance relation
  val campaignPerformance = oneToManyRelation(campaigns, campaignperformance).via((c, b) => c.id === b.campaign_id)
  // Campaign -* BudgetHistory relation
  val campaignBudgetHistory = oneToManyRelation(campaigns, budgethistory).via((c, b) => c.id === b.campaign_id)
  // Campaign -* EndDateHistory relation
  val campaignEndDateHistory = oneToManyRelation(campaigns, enddatehistory).via((c, b) => c.id === b.campaign_id)
  // Campaign -* BannerPhrase relation
  val campaignBannerPhrases = oneToManyRelation(campaigns, bannerphrases).via((c, b) => c.id === b.campaign_id)
  // Campaign -* Permutation relation
  val campaignPermutations = oneToManyRelation(campaigns, permutations).via((c, b) => c.id === b.campaign_id)

  // Banner -* BannerPhrase relation
  val bannerBannerPhrases = oneToManyRelation(banners, bannerphrases).via((b, bp) => b.id === bp.banner_id)
  // Phrase -* BannerPhrase relation
  val phraseBannerPhrases = oneToManyRelation(phrases, bannerphrases).via((b, bp) => b.id === bp.phrase_id)
  // Region -* BannerPhrase relation
  val regionBannerPhrases = oneToManyRelation(regions, bannerphrases).via((b, bp) => b.id === bp.region_id)

  // BannerPhrase -* Position relation
  val bannerPhrasePositions = oneToManyRelation(bannerphrases, positions).via((c, b) => c.id === b.bannerphrase_id)
  // BannerPhrase -* BannerPhrasePerformance relation
  val bannerPhrasePerformance = oneToManyRelation(bannerphrases, bannerphraseperformance).via((c, b) => c.id === b.bannerphrase_id)
  // BannerPhrase -* ActualBidHistory relation
  val bannerPhraseActualBidHistory = oneToManyRelation(bannerphrases, actualbidhistory).via((c, b) => c.id === b.bannerphrase_id)
  // BannerPhrase -* RecommendationHistory relation
  val bannerPhraseRecommendationHistory = oneToManyRelation(bannerphrases, recommendationhistory).via((c, b) => c.id === b.bannerphrase_id)
  // BannerPhrase -* NetAdvisedBidHistory relation
  val bannerPhraseNetAdvisedBidsHistory = oneToManyRelation(bannerphrases, netadvisedbidhistory).via((c, b) => c.id === b.bannerphrase_id)

  // Permutation -* Position relation
  val permutationPositions = oneToManyRelation(permutations, positions).via((c, b) => c.id === b.permutation_id)

  // PeriodType -* BannerPhrasePerformance relation
  val periodTypeBannerPhrasePerformance = oneToManyRelation(periodtypes, bannerphraseperformance)
    .via((c, b) => c.id === b.periodtype_id)
  // PeriodType -* CampaignPerformance relation
  val periodTypeCampaignPerformance = oneToManyRelation(periodtypes, campaignperformance)
    .via((c, b) => c.id === b.periodtype_id)

  override def drop = {
    Session.cleanupResources
    super.drop
  }

  /*// the default constraint for all foreign keys in this schema :
  import org.squeryl._
  override def applyDefaultForeignKeyPolicy(foreignKeyDeclaration: ForeignKeyDeclaration) =
    foreignKeyDeclaration.constrainReference(onDelete cascade)

  //now we will redefine some of the foreign key constraints : 
  //if we delete a subject, we want all courses to be deleted
  //campaigns.foreignKeyDeclaration.constrainReference(onDelete cascade)

  //when a course is deleted, all of the subscriptions will get deleted :
  //courseSubscriptions.leftForeignKeyDeclaration.constrainReference(onDelete cascade)
 */
}

