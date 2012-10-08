package controllers

import play.api._
import play.api.mvc._

import java.util.Date
import org.joda.time

import com.codahale.jerkson.Json

import domain._
import domain.serializing.helpers

object CampaignController extends Controller {

  /**
  * get all Campaigns for User and Network
  */
  // TODO: add links for campaigns and post new Campaign
  // GET /user/:user/net/:net/camp
  def campaigns(user_name: String, net_name: String) = Action {
    val user = User.select(user_name)
    val network = Network.select(net_name)
    (user, network) match {
      case(Nil, _) => NotFound
      case(_, Nil) => NotFound
      case _ => {
        val campaigns = Campaign.select(user.head, network.head)
        Ok(Json generate campaigns).as(JSON)
      }
    }
  }


  /**
  * get Campaign for User, Network and network_campaign_id
  */
  // GET /user/:user/net/:net/camp/:id
  def campaign(user_name: String, net_name: String, network_campaign_id: String) = Action {
    val user = User.select(user_name)
    val network = Network.select(net_name)
    (user, network) match {
      case(Nil, _) => NotFound
      case(_, Nil) => NotFound
      case _ => {
        val campaigns = Campaign.select(user.head, network.head, network_campaign_id)
        campaigns match {
          case Nil => NotFound
          case _ => Ok(Json generate campaigns).as(JSON)
        }
      }
    }
  }


  /**
  * POST Campaign for User, Network - creates new Campaign
  * @param user_name: String, net_name: String
  * @return play.api.mvc.Result
  * @through Exception if request json body has no valid representation of Campaign
  */
  // POST /user/:user/net/:net/camp
  def create_Campaign(user_name: String, net_name: String) =
    Action { request =>
    //TODO: optimize for one DB select
    val user = User.select(user_name)
    val network = Network.select(net_name)
    (user, network) match {
      case(Nil, _) => NotFound
      case(_, Nil) => NotFound
      case _ => {
        // expecting valid json
        request.body.asJson.map {jbody =>
          val js = jbody.toString
          try {
            val camp = Json.parse[Campaign](js)
            // TODO: validate data
            // relations
            camp.user_id = user.head.id
            camp.network_id = network.head.id
            // insert
            val campaign = camp.put
            // respond with created header and campaignstats body
            Created(Json generate campaign)as(JSON)
          } catch {
            case e =>
            println(e) //TODO: change to log
            BadRequest("exception caught: " + e)
          }
        }.getOrElse { BadRequest("Invalid json")}

      }
    }
  }


  /**
  * POST detailed performance report (BannerPhraseStats) for User, Network and network_campaign_id
  * @param user_name: String, net_name: String, network_campaign_id: String
  * @return play.api.mvc.Result
  * @through Exception if request json body has no valid representation of BannerPhraseStats
  */
  // POST /user/:user/net/:net/camp
  def create_Report(user_name: String, net_name: String, network_campaign_id: String) = Action { request =>
    //select Campaign
    val campaigns = Campaign.select(user_name, net_name, network_campaign_id)
    if( campaigns.isEmpty ) NotFound("""Can't find Campaign for given User: %s, Network: %s,
        network_campaign_id: %s""".format(user_name, net_name, network_campaign_id))
    else {
      val campaign = campaigns.head
      // expecting valid json
      request.body.asXml.map {body_node =>
        try {
          // TODO: ReportHelper has to be chosen dynamically
          campaign.process_report(body_node, helpers.YandexXmlReportHelper) match {
            case true => Created("Report has been created")
            case false => BadRequest("Report has NOT been created. Post it agaign if you sure that Report content is OK")
          }
        } catch {
          case e =>
          println(e) //TODO: change to log
          BadRequest("Invalid xml. Error caught: " + e)
        }
      }.getOrElse { BadRequest("Invalid xml body")}
    }
  }


  /**
  * POST CampaignStats for User, Network and network_campaign_id
  * @param user_name: String, net_name: String, network_campaign_id: String
  * @return play.api.mvc.Result
  * @through Exception if request json body has no valid representation of TimeSlot
  */
  // POST /user/:user/net/:net/camp/:id/stats
  def create_TimeSlot(user_name: String, net_name: String, network_campaign_id: String) = Action { request =>
    //select Campaign
    val campaigns = Campaign.select(user_name, net_name, network_campaign_id)
    campaigns.headOption.map { campaign =>
      // expecting valid json
      request.body.asJson.map {jbody =>
        val js = jbody.toString
        try {
          val ts = TimeSlot.parse(js)
          // insert TimeSlot
          val res_ts = campaign.insert(ts)
          // respond with CREATED header and CampaignStats body
          Created(Json generate res_ts)as(JSON)
        } catch {
          case e =>
          println(e) //TODO: change to log
          BadRequest("exception caught: " + e)
        }
      }.getOrElse { BadRequest("Invalid json body")}
    }.getOrElse { NotFound("""Can't find Campaign for given User: %s, Network: %s,
        network_campaign_id: %s""".format(user_name, net_name, network_campaign_id))
    }
  }


  /**
  * get current recommendations
  * Header If-Modified-Since must be set (preferably to the Date recieved with the last Recommendation)
  * if not Modified-Since than response Header is 304 (Not Modified) and body is empty.
  * Otherwise current recommendations in Json sent
  * recommendations are in form [{phrase_id: String, bannerID: String, regionID: String, bid: Double}]
  */
  // GET /user/:user/net/:net/camp/:id/recommendations
  def recommendations(user_name: String, net_name: String, network_campaign_id: String) = Action { request =>
    // check Header
    request.headers.get("If-Modified-Since").map {date_str =>
      // get date from String
      val fmt = time.format.ISODateTimeFormat.dateTime()
      val date: time.DateTime = fmt.parseDateTime(date_str)

      // get and check Campaign
      val campaigns = Campaign.select(user_name, net_name, network_campaign_id)
      campaigns.headOption.map { campaign =>
        //check if recommendations has been modified since
        campaign.recommendations_changed_since(date.toDate) match {
          // not changed - 304
          case false => NotModified
          // changed
          case true => {
            // retrieve recommendations from DB
            val res = campaign.select_current_recommedations(new Date)
            Ok(Json generate res)as(JSON)
          }
        }
      }.getOrElse { NotFound("""Can't find Campaign for given User: %s, Network: %s,
          network_campaign_id: %s""".format(user_name, net_name, network_campaign_id)) }
    }.getOrElse {BadRequest("Header: If-Modified-Since: yyyy-MM-dd'T'HH:mm:ss.SSSZZ has to be set")}
  }




}

