package dao.squerylorm

import org.specs2.mutable._
import org.specs2.specification._

import org.joda.time._

import dao.squerylorm.test.helpers._
import domain.po.ActualBidHistoryElem

class BannerPhraseSpec extends Specification with AllExpectations {

  "actualBidHistory" should {
    sequential
    "return ActualBidHistoryElem in ascending order and comform to given dates" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        val dao = new SquerylDao
        val bp = dao.getCampaign("Coda", "Yandex", "y1").get.bannerPhrases(0)

        val date = new DateTime
        val actualBidHistoryElems = List(
          ActualBidHistoryElem(dateTime = date, elem = 10),
          ActualBidHistoryElem(dateTime = date.plusMinutes(30), elem = 10),
          ActualBidHistoryElem(dateTime = date.plusMinutes(60), elem = 10),
          ActualBidHistoryElem(dateTime = date.plusMinutes(90), elem = 10)
        ).reverse

        //save ActualBidHistory
        actualBidHistoryElems foreach ((x:ActualBidHistoryElem) =>
          ActualBidHistory(bp = bp, ab = x).put)
        // retrieve from DB
        val campaign = dao.getCampaign("Coda", "Yandex", "y1").get
        //set histories dates
        campaign.historyStartDate = date.plusMinutes(30)
        campaign.historyEndDate = date.plusMinutes(100)
        val res = campaign.bannerPhrases(0).actualBidHistory
        res(0).dateTime must_==(actualBidHistoryElems(2).dateTime)
        res(2).dateTime must_==(actualBidHistoryElems(0).dateTime)
        res.length must_==(3)
    }}
  }


}


