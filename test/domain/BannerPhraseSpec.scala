package domain

/*
import org.specs2.mutable._
import org.specs2.specification._
*/

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.squeryl.PrimitiveTypeMode.{inTransaction}

import play.api.test._
import play.api.test.Helpers._

import com.codahale.jerkson.Json
import scala.io.Source

import domain.dao.helpers._
import domain.dao.helpers.TestDB_0



class BannerPhraseSpec extends FlatSpec with ShouldMatchers{

  "select( Campaign, network_banner_id, network_phrase_id, network_region_id)" should
    "select existing BannerPhrase" in {
      TestDB_0.running_FakeApplication() {
        val camp = Campaign.select("Coda", "Yandex", "y1").headOption
        camp should be ('defined)
        val campaign = camp.get
        // just checking that inTransaction has to used to access entity's relation directly
        inTransaction {
          val banner = campaign.banners.headOption
          banner should be ('defined)
        }

        // get BannerPhrase
        val bp = BannerPhrase.select(campaign, network_banner_id = "y_banner_1",
          network_phrase_id = "1",network_region_id = "7").headOption
        bp should be ('defined)
    }}

  it should "select Nil for non existing combination" in {
      TestDB_0.running_FakeApplication() {
        val campaign = Campaign.select("Coda", "Yandex", "y1").headOption.get
        // get BannerPhrase
        val bp = BannerPhrase.select(campaign, network_banner_id = "NO_y_banner_1",
          network_phrase_id = "1",network_region_id = "7").headOption
        bp.isEmpty should be (true)
    }}


}


