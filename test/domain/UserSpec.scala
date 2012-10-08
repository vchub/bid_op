package domain

import org.specs2.mutable._
import org.specs2.specification._

import org.squeryl.PrimitiveTypeMode.{inTransaction}

import play.api.test._
import play.api.test.Helpers._

import com.codahale.jerkson.Json

import scala.io.Source

import domain.dao.helpers._

class UserSpec extends Specification with AllExpectations{


  "User Entity" should {
    sequential

    "select all Networks" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select("Coda").head
        val networks = user.select_networks
        networks(0).name must_==("Yandex")
        networks(1).name must_==("Google")
        networks.length must_==(2)
    }}

  }


  "select(name: String)" should {
    sequential

    "select existing user" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select(name = "Coda").head
        user.name must be_==("Coda")
    }}

    "return Nil for not existing user" in {
      //running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {}
      TestDB_0.running_FakeApplication() {
        val user = User.select(name = "coda")
        user.isEmpty must_==(true)
    }}
  }


  /* edit json files if you want to use these tests
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
  */


}


