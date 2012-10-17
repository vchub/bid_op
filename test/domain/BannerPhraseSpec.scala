package domain

import org.specs2.mutable._
import org.specs2.specification._

import org.joda.time._


class BannerPhraseSpec extends Specification with AllExpectations{

  "equals" should {
    "differ w/ different ids" in {
      val bp1 = pojo.BannerPhrase(
        id = 1,
        banner = None, phrase = None, region = None,
        actualBidHistory = Nil,
        recommendationHistory = Nil,
        netAdvisedBidsHistory = Nil,
        performanceHistory = Nil
      )
      val bp2 = pojo.BannerPhrase(
        id = 2,
        banner = None, phrase = None, region = None,
        actualBidHistory = Nil,
        recommendationHistory = Nil,
        netAdvisedBidsHistory = Nil,
        performanceHistory = Nil
      )
      val perm = Map(bp1 -> 1, bp2 -> 2)
      val bp3 = pojo.BannerPhrase(
        id = 1,
        banner = None, phrase = None, region = None,
        actualBidHistory = Nil,
        recommendationHistory = Nil,
        netAdvisedBidsHistory = Nil,
        performanceHistory = Nil
      )
      // checking equality cases
      (bp1 !== bp2)
      (bp1 equals bp3)
      perm.contains(bp3) must_==(true)
      val perm1 = perm - bp1
      perm1.contains(bp1) must_==(false)
      perm1.contains(bp3) must_==(false)
    }
  }


}
