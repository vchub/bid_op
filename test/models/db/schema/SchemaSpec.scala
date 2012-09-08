package models.db.schema

//import org.squeryl.{Schema, KeyedEntity}
import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.PrimitiveTypeMode._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import play.api.test._
import play.api.test.Helpers._


case object TestDB {
  //create in memory test DB
  def use_in_memory_test_db(f: () => Unit ) = {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        AppDB.create
        f()
      }
    }
  }
}


class AppDbSpec extends FlatSpec with ShouldMatchers {

  "A CampaignStats" should "be saved and retrieved" in {
    TestDB.use_in_memory_test_db(()=>{
      val cs: CampaignStats = (CampaignStats(0,10,0)).put
      cs.id should not equal(0)
      //lazy val res = from(AppDB.campaign_stats)(a => where(a.id === cs.id) select(a))
      lazy val res = AppDB.campaign_stats.where(a => a.id === cs.id).single
      res.clicks should equal(10)
      }
    )
  }

  "A Campaign" should "be saved and retrieved by id" in {
    TestDB.use_in_memory_test_db(()=>{
      val c: Campaign = (new Campaign(0,0,0,"Google","")).put
      c.id should not equal(0)
      //lazy val res = from(AppDB.campaign_stats)(a => where(a.id === cs.id) select(a))
      lazy val res = (new Campaign()).get_by_id(c.id)
      res.network should equal("Google")
      }
    )
  }
}


class CampaignStatsSpec extends FlatSpec with ShouldMatchers {
  "A CampaignStats" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        AppDB.create
      }

      inTransaction {
        val res = AppDB.campaign_stats insert new CampaignStats(0,0,0)
        res.id should not equal(0)
      }

    }
  }
}
