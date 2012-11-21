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

  "recommendations" should {
    sequential

    "send 404 on a wrong User, Network or network_campaign_id request" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        // true parameters
        val Some(res0) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T13:00:00.000+04:00")))
        status(res0) must not equalTo(404) //SimpleResult(404, Map()))
        
        // wrong User
        val Some(res) = routeAndCall(FakeRequest(GET, "/user/User_1/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T11:00:00.000+04:00")))
        status(res) must equalTo(404) //SimpleResult(404, Map()))

        // wrong Network
        val Some(res1) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_1/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T12:00:00.000+04:00")))
        status(res1) must equalTo(404) //SimpleResult(404, Map()))

        // wrong network_campaign_id
        val Some(res2) = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_1_id/recommendations").withHeaders(
          ("If-Modified-Since", "2012-09-19T13:00:00.000+04:00")))
        status(res2) must equalTo(404) //SimpleResult(404, Map()))
        
         }
    }

    /** creates Permutations which creates Recommendations in DB*/
    def createPermutaionRecommendation(c: Campaign, dateTime: DateTime) = {

      // create optimizer object
      val opt = new Optimizer(dateTime, c)

      // create domain.Permutation
      val permutation = opt.createLocalPermutation
      // create dummy Curve
      val curve = opt.createCurve

      // save Permutation Recommendation and RecommendationChangeDate to DB
      val dao = new SquerylDao
      dao.createPermutaionRecommendation(permutation, c, curve)
    }

    "check selected Campaigns is not Nil" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        Campaign.select("User_0", "Network_0", "Net_0_Id").length must_==(1)
       // Campaign.select("User_0", "Network_0", "Net_0_Id").head must_!=(Nil)
      }
    }
    
    "respond status 304 (NOT_MODIFIED) if recommendations are not modified since" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("User_0", "Network_0", "Net_0_Id").head

        // set Date
        val date_str = "2012-10-19T13:00:10.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create 2 Recommendations entity with early and late start_date
        createPermutaionRecommendation(campaign, date.minusMinutes(1))
        createPermutaionRecommendation(campaign, date.plusMinutes(1))

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", date_str))).get
        // check status
        status(res) must equalTo(NOT_MODIFIED)
      }
    }

    "respond recommendations" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("User_0", "Network_0", "Net_0_Id").head

        // set Date
        val date_str = "2012-09-19T01:00:00.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create Recommendation entity with early start_date
        createPermutaionRecommendation(campaign, date.plusMinutes(1))

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/User_0/net/Network_0/camp/Net_0_Id/recommendations").withHeaders(
          ("If-Modified-Since", fmt.print(date)))).get

        // check status
        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        // check result
        val content = contentAsString(res)
        content must not contain ("regionID") // network_region_id
        /*// create serializers.Recommendation from result
        import com.codahale.jerkson.Json
        val rec = Json.parse[serializers.Recommendation](content)
        rec.param.length must_== (4)
        rec.param(0).CampaignID must_== (0)*/

      }
    }
  }

}