package serializers

import org.joda.time._
import com.codahale.jerkson.Json

case class Campaign(
  val network_campaign_id: String = "",
  val start_date: DateTime = new DateTime,
  val end_date: DateTime = new DateTime,
  val daily_budget: Double = 0.0,
  val _login: String = "",
  val _token: String = "") extends domain.Campaign {
  @transient
  var id: Long = 0
  @transient
  val startDate: DateTime = start_date

  @transient
  val endDate: Option[DateTime] = Some(end_date)
  @transient
  val budget: Option[Double] = Some(daily_budget)

  @transient
  var user: Option[domain.User] = None
  @transient
  var network: Option[domain.Network] = None
  @transient
  val login: Option[String] = Some(_login)
  @transient
  val token: Option[String] = Some(_token)

  @transient
  var bannerPhrases: List[domain.BannerPhrase] = Nil

  // start and end Dates of retrieved Campaign Histories
  @transient
  var historyStartDate: DateTime = new DateTime
  @transient
  var historyEndDate: DateTime = new DateTime

  @transient
  var curves: List[domain.Curve] = Nil
  @transient
  var performanceHistory: List[domain.Performance] = Nil
  @transient
  var permutationHistory: List[domain.Permutation] = Nil

  @transient
  var budgetHistory: List[domain.BudgetHistoryElem] = Nil
  @transient
  var endDateHistory: List[domain.EndDateHistoryElem] = Nil
}
object Campaign {

  /**
   * Constructor from domain.Campaign
   */
  def apply(c: domain.Campaign): Campaign = Campaign(
    network_campaign_id = c.network_campaign_id,
    start_date = c.startDate,
    end_date = c.endDate.getOrElse(new DateTime(0)),
    daily_budget = c.budget.getOrElse(0.0),
    _login = c.login.getOrElse(""), 
    _token = c.token.getOrElse(""))

  /**
   * Constructor from JSON as String
   */
  def apply(jsonString: String): Campaign = Json.parse[Campaign](jsonString)

}

