package dao.squerylorm.test.helpers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.test._
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
//import org.squeryl.PrimitiveTypeMode.view2QueryAll

import dao.squerylorm._


class TestDB_0Spec extends Specification with AllExpectations {

  "fill_DB" should {
    sequential

    "put 2 Users 2 Networks and 4 Campaigns" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.users.toList.length must_==(2)
          AppSchema.networks.toList.length must_==(2)
          AppSchema.campaigns.toList.length must_==(4)
        }
      }}


    "put 2 Banner belonging to Campaigns" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.banners.toList.length must_==(2)
          val user = User.select(name = "Coda").head
          val network = Network.select("Yandex").head
          val campaign = Campaign.select(user.name, network.name)(0)
          campaign.bannersRel.toList.length must_==(2)
        }
    }}

    "put 2 Phrases and Regions" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.phrases.toList.length must_==(2)
          AppSchema.regions.toList.length must_==(2)
        }
    }}

    "put 2 BannerPhrases belonging to Banner, Phrase and Region" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.bannerphrases.toList.length must_==(4)

          val banner = AppSchema.banners.toList(0)
          val bannerPhrases = banner.bannerPhrasesRel.toList
          bannerPhrases.length must_==(4)
          val phrase = bannerPhrases(0).phrase.head
          phrase.bannerPhrasesRel.toList.length must_==(2)
        }
    }}

    "put 2 (Curves Campaign) - (CampaignPerformance - PeriodType)" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          // select User, Network
          val user = User.select("Coda").head
          val network = Network.select("Yandex").head
          // select Campaigns
          val campaigns = Campaign.select(user.name, network.name, network_campaign_id = "y1")
          campaigns.length must_==(1)

          //Check that 2 curves belong to campaigns(0)
          val curves = campaigns(0).curves.toList
          curves.length must_==(2)

          //Check that PeriodType are put
          AppSchema.periodtypes.toList.length must_==(2)

          //Check 2 CampaignPerformance belong to campaigns(0)
          val timeSlots = campaigns(0).performancesRel.toList
          timeSlots.length must_==(2)

        }
    }}

    "put 1 Permutation and 4 Positions" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          // select User, Network
          val user = User.select("Coda").head
          val network = Network.select("Yandex").head
          // select Campaigns
          val campaigns = Campaign.select(user.name, network.name, network_campaign_id = "y1")
          // select curves
          val curves = campaigns(0).curves.toList

          // Check out that tere are 4 BannerPhrases total
          val banners = campaigns(0).bannersRel.toList
          val bannerPhrases = banners(0).bannerPhrasesRel.toList
          bannerPhrases.length must_==(4)

          // select Permutations for the Curve
          val permutations = curves(0).permutationsRel.toList
          permutations.length must_==(1)

          // find bannerPhrases for the Permutation
          permutations(0).positionsRel.toList.length must_==(4)
          permutations(0).positionsRel.head.bannerphrase_id must_==(bannerPhrases(0).id)
        }
    }}
  }


}

