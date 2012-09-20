package models.db.schema

import org.specs2.mutable._
import org.specs2.specification._

import play.api.test._
import play.api.test.Helpers._

import com.codahale.jerkson.Json
import scala.io.Source

class UserSpec extends Specification with AppHelpers{

  "User from json" should {
    "be simply loaded from json" in {
      val user = Json.parse[User]("""{"id":1,"name":"Coda"}""")
      user.name must be_==("Coda")
    }

    "be read from json file" in {
      val js_user = Source.fromFile("test/models/db/schema/json/user1.json", "utf-8").getLines.mkString
      val user = Json.parse[User](js_user)
      user.name must be_==("Coda")
    }

    "List[User] be read from json file" in {
      val js = Source.fromFile("test/models/db/schema/json/user2.json", "utf-8").getLines.mkString
      val users = Json.parse[List[User]](js)
      users.length must be_==(2)
      users(1).name must be_==("Some")
    }

    "be read as JValue and processed to List[User] and List[Network] List[Campaign]" in {
      import com.codahale.jerkson.AST._
      import com.codahale.jerkson.Json._
      val js = Source.fromFile("test/models/db/schema/json/user3.json", "utf-8").getLines.mkString
      val obj = parse[JValue](js)

      val users = parse[List[User]](generate(obj \ "users"))
      users.length must be_==(2)
      users(1).name must be_==("Some")

      val networks = parse[List[Network]](generate(obj \ "networks"))
      networks.length must be_==(2)
      networks(1).name must be_==("Yandex")

      val campaigns = parse[List[Campaign]](generate(obj \ "campaigns"))
      campaigns(0).network_name must be_==("Yandex")

    }

    "be read by Helpers.JsonSchema" in {
      val js = Source.fromFile("test/models/db/schema/json/schema.json", "utf-8").getLines.mkString
      val schema = Json.parse[JsonSchema](js)

      val users = schema.users
      users.length must be_==(2)
      users(1).name must be_==("Some")
    }
  }


  "User Entity" should {
    args(sequential=true)

    "get all campaigns" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.get_by_name("Coda")
        user.name must be_==("Coda")
        val caps = user.get_all_campaigns(network_name = "Google")
        caps.length must be_==(1)

    }}

    "get particular Campaign for network_name  campaign_network_id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.get_by_name("Coda")
        user.name must be_==("Coda")
        val c = user.get_campaign_for_net_and_net_id("Google", "go00")(0)
        c.network_campaign_id must be_==("go00")
    }}

    "be got by name" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.get_by_name("Coda")
        user.name must be_==("Coda")
    }}
  }

}


