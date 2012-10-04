package models.db.schema
package helpers

import org.specs2.mutable._
import org.specs2.specification._

import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._

import com.codahale.jerkson.Json
import scala.io.Source

import models.db.schema.helpers._

import models.db.schema._

class TestDB_0Spec extends Specification with AllExpectations {

  "fill_DB" should {
    sequential

    "put 2 Users 2 Networks and 4 Campaigns" in {
      TestDB_0.running_FakeApplication() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.users.toList.length must_==(2)
          AppSchema.networks.toList.length must_==(2)
          AppSchema.campaigns.toList.length must_==(4)
        }
      }}


    "put 2 Banner belonging to Campaigns" in {
      TestDB_0.running_FakeApplication() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.banners.toList.length must_==(2)
          val user = User.select(name = "Coda").head
          val network = Network.select("Yandex").head
          val campaign = Campaign.select(user, network)(0)
          campaign.banners.toList.length must_==(2)
        }
    }}

    "put 2 Phrases and Regions" in {
      TestDB_0.running_FakeApplication() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.phrases.toList.length must_==(2)
          AppSchema.regions.toList.length must_==(2)
        }
    }}

    "put 2 BannerPhrases belonging to Banner, Phrase and Region" in {
      TestDB_0.running_FakeApplication() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.bannerphrases.toList.length must_==(4)

          val banner = AppSchema.banners.toList(0)
          val bannerPhrases = banner.bannerPhrases.toList
          bannerPhrases.length must_==(4)
          val phrase = bannerPhrases(0).phrase.head
          phrase.bannerPhrases.toList.length must_==(2)
        }
    }}

    "put 2 (Curves Campaign) - (TimeSlot-TimeSlotType)" in {
      TestDB_0.running_FakeApplication() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          // select User, Network
          val user = User.select("Coda").head
          val network = Network.select("Yandex").head
          // select Campaigns
          val campaigns = Campaign.select(user, network, network_campaign_id = "y1")
          campaigns.length must_==(1)

          //Check that 2 curves belong to campaigns(0)
          val curves = campaigns(0).curves.toList
          curves.length must_==(2)

          //Check that timeSlotTypes are put
          AppSchema.timeslottypes.toList.length must_==(2)

          //Check 2 TimeSlots belong to curves(0)
          val timeSlots = curves(0).timeSlots.toList
          timeSlots.length must_==(2)

        }
    }}

    "put 2 Permutations" in {
      TestDB_0.running_FakeApplication() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          // select User, Network
          val user = User.select("Coda").head
          val network = Network.select("Yandex").head
          // select Campaigns
          val campaigns = Campaign.select(user, network, network_campaign_id = "y1")
          // select curves
          val curves = campaigns(0).curves.toList
          // select timeSlots
          val timeSlots = curves(0).timeSlots.toList
          timeSlots.length must_==(2)

          // Check out that tere are 4 BannerPhrases total
          val banners = campaigns(0).banners.toList
          val bannerPhrases = banners(0).bannerPhrases.toList
          bannerPhrases.length must_==(4)

          // select Permutations for the TimeSlot
          val permutations = timeSlots(1).permutations.toList
          permutations.length must_==(2)

          // find bannerPhrases for the Permutation
          permutations(0).bannerPhrase.head.banner_id must_==(banners(0).id)
        }
    }}
  }


}

