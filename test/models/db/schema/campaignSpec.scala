package models.db.schema

import org.specs2.mutable._
import org.specs2.specification._

import play.api.test._
import play.api.test.Helpers._


class CampaignSpec extends Specification with AllExpectations {

  "Campaign Entity" should {
    sequential

    "be got by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        camp.id must be_==(1)
    }}

    "be put in DB" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign(user_name = "Coda", network_name = "Yandex",
            network_campaign_id = "y44")
        camp.id must be_==(0)
        val res :Campaign = camp.put
        res.id must_!=(0L)
        res.network_name must_==("Yandex")
    }}

    "get banners" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val banners = camp.get_banners
        banners(0).campaign_id must be_==(1)
        banners(1).id must be_==(2)
    }}

    "get banner_phrases" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val camp = Campaign get_by_id 1
        val banner_phrases = camp.get_banner_phrases
        banner_phrases(0).banner_id must be_==(1)
        banner_phrases(0).phrase_id must be_==(1)
    }}


  }
}


