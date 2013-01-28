package controllers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.libs.json.{ JsValue, Json, JsObject }
import play.api.test._
import play.api.test.Helpers._

import org.joda.time._
import scala.xml._

import domain.{ User, Campaign, Network }
import dao.squerylorm._
import dao.squerylorm.test.helpers._

import optimizer.Optimizer

class CampaignControllerSpec1 extends Specification with AllExpectations {

  val dao = new SquerylDao
  val midnnight_formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd")
  //set startDate and endDate
  val dateStart = midnnight_formatter.parseDateTime("2012-09-19")
  val dateEnd = dateStart.plusMinutes(30 * (48 * 10 + 24))

  "recommendations" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        // true parameters
        val Some(res0) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since" -> "2012-09-19T13:00:00.000+04:00"), ("password" -> "123")))
        status(res0) must not equalTo (404) //SimpleResult(404, Map()))        

        // wrong User
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/User_1/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T11:00:00.000+04:00"), ("password" -> "123")))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_1/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T12:00:00.000+04:00"), ("password" -> "123")))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_1_id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T13:00:00.000+04:00"), ("password" -> "123")))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
        
        // wrong password
        val Some(res3) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since" -> "2012-09-19T13:00:00.000+04:00"), ("password" -> "123456789")))
        status(res3) must equalTo (404) //NotFound
        
        val Some(res4) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since" -> "2012-09-19T13:00:00.000+04:00")))
        status(res4) must equalTo (400) //BadRequest

      }
    }

    /** creates Permutations which creates Recommendations in DB*/
    def createPermutaionRecommendation(c: Campaign, dateTime: DateTime) = {

      // create optimizer object 
      val opt = new Optimizer

      // create domain.Permutation
      val permutation = opt.createLocalPermutation(c, dateTime)

      // save Permutation Recommendation and RecommendationChangeDate to DB
      //val dao = new SquerylDao
      val dt = dao.createPermutaionRecommendation(permutation, c, c.curves.head)

      // create dummy Curve
      val curve = opt.createCurve(c, dateTime)

      dt
    }

    "check selected Campaigns is not Nil" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = dao.getCampaign("User_0", "Network_0", "Net_0_Id", dateStart, dateEnd).get
        campaign must_!= (Nil)
      }
    }

    "respond status 304 (NOT_MODIFIED) if recommendations are not modified since" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = dao.getCampaign("User_0", "Network_0", "Net_0_Id", dateStart, dateEnd).get

        // create 2 Recommendations entity with early and late start_date
        createPermutaionRecommendation(campaign, dateEnd.plusMinutes(1)) must_== (dateEnd.plusMinutes(1))

      }
    }
  }

}