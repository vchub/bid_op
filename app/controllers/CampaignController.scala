package controllers

import play.api._
import play.api.mvc._
import org.joda.time._

import play.api.libs.json._

import domain.{ User, Campaign, Network }
import dao.squerylorm.SquerylDao
import serializers.yandex.XmlReport

object CampaignController extends Controller with Secured {

  /**
   * get all Campaigns for User and Network
   */
  // TODO: add links for campaigns and post new Campaign
  // GET /user/:user/net/:net/camp
  def campaigns(user_name: String, net_name: String) = IsAuth(
    user_name,
    (dao, user) => implicit request => {
      dao.getCampaigns(user_name, net_name) match {
        case Nil => NotFound("CAMPAIGNS are NOT FOUND...")
        case campaigns => 
          val sCampaigns = campaigns map (serializers.Campaign._apply(_))
          Ok(Json.toJson(sCampaigns)(json_api.Writes.campaignList)).as(JSON)
      }
    })

  /**
   * get Campaign for User, Network and network_campaign_id
   * GET /user/:user/net/:net/camp/:id
   */
  def campaign(user_name: String, net_name: String, network_campaign_id: String) = IsAuth(
    user_name,
    (dao, user) => implicit request => {
      dao.getCampaign(user_name, net_name, network_campaign_id) match {
        case None => NotFound("CAMPAIGN is NOT FOUND...")
        case Some(c) =>
          val sCampaign = serializers.Campaign._apply(c)
          Ok(Json.toJson(List(sCampaign))(json_api.Writes.campaignList)).as(JSON)
      }
    })

  /**
   * POST Campaign for User, Network - creates new Campaign
   * @param user_name: String, net_name: String
   * @return play.api.mvc.Result
   * @through Exception if request json body has no valid representation of Campaign
   * POST /user/:user/net/:net/camp
   */
  def createCampaign(user_name: String, net_name: String) = IsAuth(
    user_name,
    (dao, user) => request => {
      //TODO: optimize for one DB select
      dao.getNetwork(net_name) match {
        case None => NotFound("network not found")
        case network => {
          // expecting valid json 
          println("----------------------------------------------------------------------------")
          request.body.asJson match {
            case None => BadRequest("Invalid json body")
            case Some(jbody) =>
              try {
                //Create Campaign
                val c = serializers.Campaign._apply(jbody)
                c.user = Some(user)
                c.network = network
                // insert Campaign
                val domCamp = dao.create(c)

                // respond with CREATED header and Campaign body
                Created(Json.toJson(serializers.Campaign._apply(domCamp))(json_api.Writes.campaign)) as (JSON)
              } catch {
                case e =>
                  println(e) //TODO: change to log
                  BadRequest("exception caught: " + e)
              }
          }
        }
      }
    })

  /**
   * recieve stats in the END of the day!
   * POST detailed performance report (BannerPhraseStats) for User, Network and network_campaign_id
   * @param user_name: String, net_name: String, network_campaign_id: String
   * @return play.api.mvc.Result
   * @through Exception if request json body has no valid representation of BannerPhraseStats
   * POST /user/:user/net/:net/camp
   */
  def createXmlReport(user_name: String, net_name: String, network_campaign_id: String) = IsAuth(
    user_name,
    (dao, user) => request => {
      //select Campaign
      dao.getCampaign(user_name, net_name, network_campaign_id) match {
        case None => NotFound("""Can't find Campaign for given User: %s, Network: %s,
          network_campaign_id: %s""".format(user_name, net_name, network_campaign_id))
        case Some(c) =>
          request.body.asXml match {
            case None => BadRequest("Invalid xml body")
            case Some(body_node) =>
              try {
                // TODO: ReportHelper has to be chosen dynamically
                val report = (new XmlReport(body_node)).createBannerPhrasePerformanceReport

                //save report in DB
                dao.createBannerPhrasesPerformanceReport(c, report) match {
                  case true => Created("Report has been created")
                  case false => BadRequest("Report has NOT been created. Post it agaign if you sure that Report content is OK")
                }
              } catch {
                case e =>
                  //e.printStackTrace
                  println(e) //TODO: change to log
                  BadRequest("Invalid xml. Error caught: " + e)
              }
          }
      }
    })

  /**
   * recieve stats DURING the day! CREATE INITIAL PERMUTATION, CURVE, RECOMMENDATION,...
   * for - GetStats - BUTTON
   * POST Campaign Performance for User, Network and network_campaign_id
   * @param user_name: String, net_name: String, network_campaign_id: String
   * @return play.api.mvc.Result
   * @through Exception if request json body has no valid representation of TimeSlot
   * POST /user/:user/net/:net/camp/:id/stats
   */
  def createCampaignPerformance(user_name: String, net_name: String, network_campaign_id: String) = IsAuth(
    user_name,
    (dao, user) => request => {
      //select Campaign
      dao.getCampaign(user_name, net_name, network_campaign_id) match {
        case None => NotFound("""Can't find Campaign for given User: %s, Network: %s,
          network_campaign_id: %s""".format(user_name, net_name, network_campaign_id))
        case Some(c) =>
          request.body.asJson match {
            case None => BadRequest("Invalid json body")
            case Some(jbody) =>
              try {
                println("!!!!!!!!!!!!! FAKE !!!!!!!!!!!!!")
                val performance = serializers.Performance._apply(jbody)
                // insert Performance
                // TODO: no PeriodType now. Fix.
                println("SERIALIZED PERFORMANCE!!!!!!!!!!!!!" + c.toString() + "&&&&&&&&&" + performance.toString())
                val domPerf = dao.createCampaignPerformanceReport(c, performance)
                // respond with CREATED header and Performance body
                println("CREATED PERFORMANCE!!!!!!!!!!!!!!")

                /** Create Permutation-Recommendation **/
                c.historyStartDate = c.startDate
                c.historyEndDate = c.endDate.getOrElse(new DateTime())

                println("<<<<< " + c.permutationHistory.toString + " >>>>>")
                println("<<<<< " + c.curves.toString + " >>>>>")

                if (runOptimizerAlgorithm(c, performance, dao))
                  println("CREATED PERMUTATION-RECOMMENDATION!!!!!!!!!!!!!!")
                else
                  println("Algorithm is FAILED!!!!!!!!!!!!")

                Created(Json.toJson(serializers.Performance._apply(domPerf))(json_api.Writes.performance)) as (JSON)
              } catch {
                case e =>
                  e.printStackTrace //TODO: change to log
                  BadRequest("exception caught: " + e)
              }
          }
      }
    })

  def runOptimizerAlgorithm(c: Campaign, performance: serializers.Performance, dao: SquerylDao): Boolean = {
    try {
      val PR = c.permutationHistory match {
        case Nil => {
          // create Permutation Map[BannerPhrase, Position]
          val permutation_map = (
            for {
              (b, i) <- c.bannerPhrases.zipWithIndex
            } yield (b, domain.po.Position(i))).toMap

          // create domain.Permutation  (DB Positions is created in Permutation!!!)
          val permutation = new domain.po.Permutation(0, dateTime = performance.dateTime, permutation = permutation_map)

          // create dummy Curve
          val curve = dao.create(
            curve = new domain.po.Curve(0, 1, 1, 1, 1, performance.dateTime, Some(permutation)),
            campaign = c)
          // save Permutation Recommendation and RecommendationChangeDate to DB
          dao.createPermutaionRecommendation(permutation, c, curve)
        }
        case cList => {
          import optimizer._
          val opt = new Optimizer
          val loc_permutation = opt.createLocalPermutation(c, performance.dateTime)
          dao.createPermutaionRecommendation(loc_permutation, c, c.curves.head)
        }
      }
      true
    } catch {
      case t: Throwable => false
    }
  }

  /**
   * Actual Bids and NetAdvised Bids! CREATE INITIAL BANNERS,PHRASES,BANNERPHRASES,...
   * for - Get ActualBids and NetAdvisedBids - BUTTON
   * POST analogous to getBanners (Live) Yandex report
   * it creates ActualBidHistory and NetAdvisedBidHistory records
   * @param user_name: String, net_name: String, network_campaign_id: String
   * @return play.api.mvc.Result
   * @through Exception if request json body has no valid representation of TimeSlot
   * POST /user/:user/net/:net/camp/:id/bannerreports
   */
  def createBannerReport(user_name: String, net_name: String, network_campaign_id: String) = IsAuth(
    user_name,
    (dao, user) => request => {
      //select Campaign
      dao.getCampaign(user_name, net_name, network_campaign_id) match {
        case None => NotFound("""Can't find Campaign for given User: %s, Network: %s,
          network_campaign_id: %s""".format(user_name, net_name, network_campaign_id))
        case Some(c) =>
          request.body.asJson match {
            case None => BadRequest("Invalid json body")
            case Some(jbody) =>
              try {
                val br = serializers.yandex.BannerReport._apply(jbody)
                val l = println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                val report = br.getDomainReport
                val q = println("$$$$$$$$$$$$$$$$$$$$$$$$")
                // save in DB
                val res = dao.createBannerPhraseNetAndActualBidReport(c, report)
                // respond with CREATED header and res: Boolean
                Created(Json.toJson(res)) as (JSON)
              } catch {
                case e =>
                  println(e) //TODO: change to log
                  //e.printStackTrace
                  BadRequest("exception caught: " + e)
              }
          }
      }
    })

  /**
   * get current recommendations
   * Header If-Modified-Since must be set (preferably to the Date recieved with the last Recommendation)
   * if not Modified-Since than response Header is 304 (Not Modified) and body is empty.
   * Otherwise current recommendations in Json sent
   * recommendations are in form [{phrase_id: String, bannerID: String, regionID: String, bid: Double}]
   * GET /user/:user/net/:net/camp/:id/recommendations
   */
  def recommendations(user_name: String, net_name: String, network_campaign_id: String) = IsAuth(
    user_name,
    (dao, user) => request => {
      request.headers.get("If-Modified-Since") match {
        case None => BadRequest("Header: If-Modified-Since: yyyy-MM-dd'T'HH:mm:ss.SSSZZ has to be set")
        case Some(date_str) =>
          // get date from String
          val date: DateTime = format.ISODateTimeFormat.dateTime().parseDateTime(date_str)
          println(date)
          //select Campaign
          val dao = new SquerylDao
          dao.getCampaign(user_name, net_name, network_campaign_id) match {
            case None => {
              println("!!!!Not found campaigns");
              NotFound("""Can't find Campaign for given User: %s, Network: %s,
              network_campaign_id: %s""".format(user_name, net_name, network_campaign_id))
            }
            case Some(c) => {
              println("!!!!Found campaigns");
              //check if recommendations has been modified since
              dao.recommendationChangedSince(c, date) match {
                // not changed - 304
                case false => NotModified // retrieve recommendations from DB
                /*dao.getCurrentRecommedation(c, date) match {
                  case None => {
                    println("false NOT found current recommendations!!!!");
                    BadRequest("No Recommendation found")
                  }
                  case Some(rec) => {
                    println("NOT Changed!!!!");
                    Ok(serializers.Recommendation(c, rec).getAsJson) as (JSON)
                  }
                }*/
                // changed
                case true =>
                  // retrieve recommendations from DB
                  dao.getCurrentRecommedation(c) match {
                    case None => {
                      println("true NOT found current recommendations!!!!");
                      BadRequest("No Recommendation found")
                    }
                    case Some(rec) => {
                      println("YES Changed!!!!");
                      Ok(serializers.Recommendation(c, rec).getAsJson) as (JSON)
                    }
                  }
              }
            }

          }

      }
    })

}

