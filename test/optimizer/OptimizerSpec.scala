package optimizer

import org.specs2.mutable._
import org.specs2.specification._
import play.api.mvc._
import play.api._
import play.api.libs.json.{ JsValue, Json, JsObject }
import play.api.test._
import play.api.test.Helpers._
import org.joda.time
import scala.xml._
import domain.{ User, Campaign, Network }
import dao.squerylorm._
import dao.squerylorm.test.helpers._
import optimizer._
import dao.squerylorm.Permutation
import domain.Permutation
import domain.po.Permutation

class OptimizerSpec extends Specification with AllExpectations {

  val dao = new SquerylDao
  val opt = new Optimizer

  val midnnight_formatter = time.format.DateTimeFormat.forPattern("yyyy-MM-dd")
  //set startDate and endDate
  val dateStart = midnnight_formatter.parseDateTime("2012-09-19")
  val dateEnd = dateStart.plusMinutes(30 * (48 * 10 + 24))
  
  val NumberOfBanners = 5
  val NumberOfPhrasesInBanner = 10

  "optimizer" should {
    sequential

    "create local permutation map" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = dao.getCampaign("User_0", "Network_0", "Net_0_Id", dateStart, dateEnd).get
        val NumOfSegm = 10

        //create new optimizer object
        val (permutation, ind) = opt.createLocalPermutationMap(campaign, NumOfSegm)
        opt.PermutationMeasure(ind) must beGreaterThanOrEqualTo((NumberOfBanners*NumberOfPhrasesInBanner/NumOfSegm-1).toDouble)
        //campaign.historyStartDate must_!= campaign.historyEndDate

      }
    }

    "create optimal permutation map" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = dao.getCampaign("User_0", "Network_0", "Net_0_Id", dateStart, dateEnd).get

        //create new optimizer object
        val permutation = opt.createOptimalPermutationMap(campaign)
        campaign.historyStartDate must_!= campaign.historyEndDate

      }
    }
  }
}