package dao.squerylorm

import org.specs2.mutable._
import org.specs2.specification._

import org.joda.time._
import java.sql.Timestamp

import dao.squerylorm.test.helpers._

class ActualBidHistorySpec extends Specification with AllExpectations {

  "apply" should {
    sequential
    "create squerylorm.ActualBidHistory" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val bp = dao.getCampaign("Coda", "Yandex", "y1").get.bannerPhrases(0)

        val actualBidHistoryElem = domain.po.ActualBidHistoryElem(dateTime = new DateTime, elem = 10)
        val abh = ActualBidHistory(bp = bp, ab = actualBidHistoryElem).put
        var res = ActualBidHistory.get_by_id(abh.id)
        res.dateTime must_==(actualBidHistoryElem.dateTime)
        res.elem must_==(actualBidHistoryElem.elem)
    }}
  }


}


