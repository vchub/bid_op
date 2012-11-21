package dao.squerylorm.test.helpers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.test._
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
//import org.squeryl.PrimitiveTypeMode.view2QueryAll

import dao.squerylorm._

class TestDB_1Spec extends Specification with AllExpectations {
  val NumberOfUser = 1
  val NumberOfNetworks = 1
  val NumberOfCampaigns = 1
  val NumberOfRegions = 1

  val NumberOfBanners = 5
  val NumberOfPhrasesInBanner = 10 //

  val NumOfDays = 10 //how many days passed
  val NumOfTimestamps = 48 * NumOfDays + 24
  //how many timestamps passed in a last day
  //1 timestamp = 30 min => 48 timestamps in a day

  "fill_DB" should {
    sequential

    "put NumberOfUser Users, NumberOfNetworks Networks and NumberOfCampaigns Campaign" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.users.toList.length must_== (NumberOfUser)
          AppSchema.networks.toList.length must_== (NumberOfNetworks)
          AppSchema.campaigns.toList.length must_== (NumberOfCampaigns)
        }
      }
    }

    "put NumberOfBanners Banners belonging to Campaigns" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.banners.toList.length must_== (NumberOfBanners)
          val user = User.select(name = "User_0").head
          val network = Network.select("Network_0").head
          val campaign = Campaign.select(user.name, network.name)(0)
          campaign.bannerPhrasesRel.toList.groupBy(_.banner.get.network_banner_id).size must_== (NumberOfBanners)
        }
      }
    }

    "put NumberOfPhrasesInBanner*NumberOfBanners Phrases and NumberOfRegions Region" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.phrases.toList.length must_== (NumberOfPhrasesInBanner * NumberOfBanners)
          AppSchema.regions.toList.length must_== (NumberOfRegions)
        }
      }
    }

    "put NumberOfPhrasesInBanner*NumberOfBanners BannerPhrases belonging to Banner, Phrase and Region" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          AppSchema.bannerphrases.toList.length must_== (NumberOfPhrasesInBanner * NumberOfBanners)

          val banner = AppSchema.banners.toList(0)
          val bannerPhrases = banner.bannerPhrasesRel.toList
          bannerPhrases.length must_== (NumberOfPhrasesInBanner)
          val phrase = bannerPhrases(0).phrase.head
          phrase.bannerPhrasesRel.toList.length must_== (1)
        }
      }
    }

    "put NumOfDays (Curves Campaign) - (CampaignPerformance - PeriodType)" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          // select User, Network
          val user = User.select("User_0").head
          val network = Network.select("Network_0").head
          // select Campaigns
          val campaigns = Campaign.select(user.name, network.name, network_campaign_id = "Net_0_Id")
          campaigns.length must_== (1)

          //Check that NumOfDays curves belong to campaigns(0)
          val curves = campaigns(0).curvesRel.toList
          curves.length must_== (NumOfDays)

          //Check that PeriodType are put
          AppSchema.periodtypes.toList.length must_== (2)

          //Check 2 CampaignPerformance belong to campaigns(0)
          val timeSlots = campaigns(0).performancesRel.toList
          timeSlots.length must_== (NumOfTimestamps)

        }
      }
    }

    "put NumOfTimestamps Permutation and NumOfTimestamps*NumberOfPhrasesInBanner*NumberOfBanners Positions" in {
      TestDB_1.creating_and_filling_inMemoryDB() {
        inTransaction {
          import org.squeryl.PrimitiveTypeMode._
          // select User, Network
          val user = User.select("User_0").head
          val network = Network.select("Network_0").head
          // select Campaigns
          val campaigns = Campaign.select(user.name, network.name, network_campaign_id = "Net_0_Id")

          // Check out that there are 4 BannerPhrases total
          val bannerPhrases = campaigns(0).bannerPhrasesRel.toList
          bannerPhrases.length must_== (NumberOfPhrasesInBanner * NumberOfBanners)

          // select Permutations for the Curve
          val permutations = campaigns(0).permutationsRel.toList
          permutations.length must_== (NumOfTimestamps)

          // find bannerPhrases for the Permutation
          permutations(0).positionsRel.toList.length must_== (NumberOfPhrasesInBanner * NumberOfBanners)
          permutations(0).positionsRel.head.bannerphrase_id must_== (bannerPhrases(0).id)
        }
      }
    }

  }

}

