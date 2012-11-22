package optimizer

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
import optimizer._
import domain.po.Permutation
import domain.po.Permutation
import domain.Permutation
import dao.squerylorm.Permutation
import domain.Permutation
import domain.po.Permutation

class OptimizerSpec extends Specification with AllExpectations {

  "optimizer" should {
    sequential

    def PermutationMeasure(IndPerm: List[Int]): Double = {
      /**
       * Calculate the difference between initial and new positions
       * PosPerm is position permutation related to optimal positions
       */

      val diffVector = 0 until IndPerm.length zip IndPerm map {
        case (initInd, newInd) => math.abs(initInd - newInd)
      }
      (0 /: diffVector)(_ + _) / 2

      /** We calculate the sum of position deviations */
    }

    "create local permutation map" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        val campaign = Campaign.select("User_0", "Network_0", "Net_0_Id").head

        // set Date
        val date_str = "2012-10-19T13:00:10.000+04:00"
        val fmt = format.ISODateTimeFormat.dateTime()
        val date: DateTime = fmt.parseDateTime(date_str)

        //crreate new optimizer object
        val opt = new Optimizer(date, campaign)
        opt.createLocalPermutationMap(10)

        // make a request
        val res = routeAndCall(FakeRequest(GET, "/user/Coda/net/Yandex/camp/y1/recommendations").withHeaders(
          ("If-Modified-Since", date_str))).get
        // check status
        status(res) must equalTo(NOT_MODIFIED)
      }
    }

  }
}