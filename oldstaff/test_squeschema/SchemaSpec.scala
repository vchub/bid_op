package dao.squschema

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.Session

import org.specs2.mutable._
import org.specs2.specification._

import play.api.test._
import play.api.test.Helpers._

/*
class SchemaSpec extends Specification with AllExpectations {

  "JsonSchema" should {


    "be loaded from json file" in {
        val schema = JsonSchema read_file "test/models/db/schema/json/schema.json"
        val users = schema.users
        users(0).name must be_==("Coda")
      }
  }


}

*/

