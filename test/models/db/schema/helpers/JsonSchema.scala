package models.db.schema
package helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLInnoDBAdapter}
import org.squeryl.{Session, SessionFactory}

import play.api.test._
import play.api.test.Helpers._
import play.api.Play

import com.mysql.jdbc.Driver
import org.h2.Driver

import com.codahale.jerkson.Json
import scala.io.Source



case class JsonSchema(
  val networks :List[Network],
  val users :List[User],
  val campaigns :List[Campaign]
)
  {
  }

object JsonSchema extends AppHelpers {
  def read_file(file_name: String) = {
    val js = Source.fromFile(file_name, "utf-8").getLines.mkString
    Json.parse[JsonSchema](js)
  }

  def put(js_schema: JsonSchema) {
    import AppSchema._
    inTransaction {
      for(n <- js_schema.networks) n.save
      for(u <- js_schema.users) u.save
      for(c <- js_schema.campaigns) c.save
    }
  }

  def fill_DB() {
    val schema = JsonSchema read_file "test/models/db/schema/json/schema.json"
    // put to db
    JsonSchema put schema
  }



  def populate_test_postgres_db(json_file_name: String = "test/models/db/schema/json/schema.json") {
    prepare_postgresql_test_db()
    inTransaction {
      AppSchema.create
      val schema = JsonSchema read_file json_file_name
      put( schema )
    }
  }

}

