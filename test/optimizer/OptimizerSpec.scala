package optimizer

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.test._
import play.api.test.Helpers._
import org.joda.time._
import scala.xml._
import domain.{User, Campaign, Network}
import dao.squerylorm._
import dao.squerylorm.test.helpers._
import optimizer._

class OptimizerSpec extends Specification with AllExpectations{

  "optimizer" should {
    sequential

    "respond status 304 (NOT_MODIFIED) if recommendations are not modified since" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head

        // set Date
        val date_str = "2012-10-19T13:00:10.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create 2 Recommendations entity with early and late start_date
        createPermutaionRecommendation(campaign, date.minusMinutes(1))
        createPermutaionRecommendation(campaign, date.plusMinutes(1))

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", date_str))).get
        // check status
        status(res) must equalTo(NOT_MODIFIED)
      }
    }
    
    /** creates Permutations which creates Recommendations in DB*/ 
    def createPermutaionRecommendation(c: Campaign, dateTime: DateTime) = {
      
      // create optimizer object
      val opt = new Optimizer(dateTime,c) 
      
      // create domain.Permutation
      val permutation = opt.createLocalPermutation
      // create dummy Curve
      val curve = opt.createCurve
      
      // save Permutation Recommendation and RecommendationChangeDate to DB
      val dao = new SquerylDao
      dao.createPermutaionRecommendation(permutation, c, curve)
    }

    "respond recommendations" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").head

        // set Date
        val date_str = "2012-09-19T01:00:00.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        // create Recommendation entity with early start_date
        createPermutaionRecommendation(campaign, date.plusMinutes(1))

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", fmt.print(date) ))).get

        // check status
        status(res) must equalTo(OK)
        contentType(res) must beSome.which(_ == "application/json")
        // check result
        val content = contentAsString(res)
        content must not contain("regionID") // network_region_id
        // create serializers.Recommendation from result
        import com.codahale.jerkson.Json
        val rec = Json.parse[serializers.Recommendation](content)
        rec.param.length must_==(4)
        rec.param(0).CampaignID must_==(1)

      }
    }
  }
}