package controllers

import play.api._
import play.api.mvc._
import org.joda.time._

import json_api.Convert._
import play.api.libs.json._

import domain.{ User, Campaign, Network }
import dao.squerylorm.{ SquerylDao, Charts }
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
          val sCampaigns = campaigns map { c =>
            c.historyStartDate = c.startDate
            serializers.Campaign._apply(c)
          }
          Ok(toJson[List[serializers.Campaign]](sCampaigns)) as (JSON)
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
          c.historyStartDate = c.startDate
          val sCampaign = serializers.Campaign._apply(c)
          Ok(toJson[List[serializers.Campaign]](List(sCampaign))) as (JSON)
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
          request.body.asJson match {
            case None => BadRequest("Invalid json body")
            case Some(jbody) =>
              try {
                //Create Campaign
                fromJson[serializers.Campaign](jbody) map { c =>
                  c.user = Some(user)
                  c.network = network
                  // insert Campaign
                  val domCamp = dao.create(c)

                  // respond with CREATED header and Campaign body
                  Created(toJson[serializers.Campaign](serializers.Campaign._apply(domCamp))) as (JSON)
                } getOrElse BadRequest
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
                  case true =>
                    println("!!!!!!!!!!!!!!! CREATED XmlReport !!!!!!!!!!!!!!")
                    Created("Report has been created")
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
   * recieve Banners Stats DURING the day! CREATE INITIAL PERMUTATION, CURVE, RECOMMENDATION,...
   * for - GetStats - BUTTON
   * POST Banners Performance for User, Network and network_campaign_id
   * @param user_name: String, net_name: String, network_campaign_id: String
   * @return play.api.mvc.Result
   * @through Exception if request json body has no valid representation of TimeSlot
   * POST /user/:user/net/:net/camp/:id/bannersstats
   */
  def createBannersPerformance(user_name: String, net_name: String, network_campaign_id: String) = IsAuth(
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
                val cur_dt = request.headers.get("current_datetime") map { dt =>
                  format.ISODateTimeFormat.dateTime().parseDateTime(dt)
                } getOrElse (new DateTime())

                fromJson[serializers.GetBannersStatResponse](jbody) map { bsr =>
                  val report = serializers.BannersPerformance.createBannerPhrasePerformanceReport(bsr, c, cur_dt)

                  //save report in DB
                  dao.createBannerPhrasesPerformanceReport(c, report) match {
                    case true =>
                      println("!!!!!!!!!!!!!!! CREATED BannersPERFORMANCE !!!!!!!!!!!!!!")
                      Created("Report has been created")
                    case false => BadRequest("Report has NOT been created. Post it agaign if you sure that Report content is OK")
                  }
                } getOrElse BadRequest("Report has NOT been serialized. Post it agaign if you sure that Report content is OK")
              } catch {
                case e =>
                  e.printStackTrace //TODO: change to log
                  BadRequest("exception caught: " + e)
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
                fromJson[serializers.Performance](jbody) map { performance =>
                  /* it needs Dao to find out the PeriodType */
                  performance.periodType = dao.getPeriodType(performance.dateTime)

                  // insert Performance
                  // TODO: no PeriodType now. Fix.
                  val domPerf = dao.createCampaignPerformanceReport(c, performance)
                  println("CREATED PERFORMANCE!!!!!!!!!!!!!!")

                  /** Create Permutation-Recommendation **/
                  /*c.historyStartDate = c.startDate
                  c.historyEndDate = c.endDate.getOrElse(new DateTime())

                  println("<<<<< " + c.permutationHistory.toString + " >>>>>")
                  println("<<<<< " + c.curves.toString + " >>>>>")
                  if (runOptimizerAlgorithm(c, performance, dao))
                    println("CREATED PERMUTATION-RECOMMENDATION!!!!!!!!!!!!!!")
                  else
                    println("Algorithm is FAILED!!!!!!!!!!!!")*/

                  // respond with CREATED header and Performance body
                  Created(toJson[serializers.Performance](serializers.Performance._apply(domPerf))) as (JSON)
                } getOrElse BadRequest
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
   * ActualBids and NetAdvised Bids! CREATE INITIAL BANNERS,PHRASES,BANNERPHRASES,...
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
                fromJson[List[serializers.yandex.BannerInfo]](jbody) map { bil =>
                  val report = serializers.yandex.BannerReport.getDomainReport(bil)

                  // save in DB
                  val res = dao.createBannerPhraseNetAndActualBidReport(c, report)

                  val q = println("$$$$$$$$$$$$ CREATED BannerReport $$$$$$$$$$$$")

                  // respond with CREATED header and res: Boolean
                  Created(Json.toJson(res)) as (JSON)
                } getOrElse BadRequest
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
                      Ok(toJson[List[serializers.PhrasePriceInfo]](serializers.Recommendation(c, rec))) as (JSON)
                    }
                  }
              }
            }

          }

      }
    })

  /**
   * generate CHARTS in view for User, Network and network_campaign_id
   * GET /user/:user/net/:net/camp/:id/charts
   */
  def charts(user_name: String, net_name: String, network_campaign_id: String, password: String) =
    Action { implicit request => //TODO - need to add Authentication!!!
      import scala.concurrent.Future
      import scala.concurrent.ExecutionContext.Implicits.global
      import java.util.concurrent.TimeUnit

      //send keep alive request to client
      import play.api.libs.concurrent.Akka
      import scala.concurrent.duration._
      import play.api.Play.current
      import play.api.libs.ws.WS

      val keepAlive = Akka.system.scheduler.schedule(10 seconds, 10 seconds) {
        //send head request to the client to keep alive
        WS.url(request.headers.get("Referer").get).head().onSuccess {
          case _ => println("!!! wake up client !!!" + request.headers.get("Referer").get)
        }
      }

      val futureResult = Future[Result] {

        val dao = new SquerylDao()

        dao.getUser(user_name, password) match {
          case None => NotFound("User NOT FOUND... Invalid name or password...")
          case Some(user) => {
            dao.getCampaign(user_name, net_name, network_campaign_id) match {
              case None => NotFound("CAMPAIGN is NOT FOUND...")
              case Some(c) =>
                val iso_fmt = format.ISODateTimeFormat.dateTime()
                val sdate = iso_fmt.parseDateTime("1000-01-01T12:00:00.000+04:00")
                val edate = iso_fmt.parseDateTime("3000-01-01T12:00:00.000+04:00")
                //we will retrieve all data from db 
                c.historyStartDate = sdate
                c.historyEndDate = edate

                /*
                 * start Consuming Computations - retrieve data from DB and calcs CTR
                 */

                val cCTR_List = Charts.getCampaignCTR(Some(c))

                val b_List = Charts.getBudget(Some(c))

                val pp_List = c.bannerPhrases map { bp =>
                  bp.id -> Charts.getPositionPrices(Some(c), bp.id)
                } toMap

                val bpCTR_List = c.bannerPhrases map { bp =>
                  bp.id -> Charts.getBannerPhraseCTR(Some(c), bp.id)
                } toMap

                /* ends */

                //generate charts in browser
                Ok(views.html.charts(Some(c), cCTR_List, b_List, pp_List, bpCTR_List))
            }
          }
        }
      }

      // if service handles request too slow => return Timeout response
      val timeoutFuture = play.api.libs.concurrent.Promise.timeout(
        message = "Oops, TIMEOUT while calling BID server...",
        duration = 3,
        unit = TimeUnit.MINUTES)

      Async {
        Future.firstCompletedOf(Seq(futureResult, timeoutFuture)).map {
          case f: Result =>
            keepAlive.cancel
            f
          case t: String =>
            keepAlive.cancel
            InternalServerError(t)
        }
      }
    }

  /*IsAuth(
    user_name,
    (dao, user) => implicit request => {
      dao.getCampaign(user_name, net_name, network_campaign_id) match {
        case None => NotFound("CAMPAIGN is NOT FOUND...")
        case Some(c) =>
          val iso_fmt = format.ISODateTimeFormat.dateTime()
          val sdate = iso_fmt.parseDateTime("1000-01-01T12:00:00.000+04:00")
          val edate = iso_fmt.parseDateTime("3000-01-01T12:00:00.000+04:00")
          //we will retrieve all data from db 
          c.historyStartDate = sdate
          c.historyEndDate = edate

          //generate charts in browser
          Ok(views.html.charts(Some(c)))
      }
    })*/

}

