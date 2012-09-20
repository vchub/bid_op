package models.db.schema

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter}
import org.squeryl.{Session, SessionFactory}

import play.api.test._
import play.api.test.Helpers._

import org.h2.Driver

import com.codahale.jerkson.Json
import scala.io.Source

trait AppHelpers {
  /**
  * creates in memory DB and schema
  * runs block of code in squeryl transaction
  * @param block of code
  * @return T
  */
  def running_in_memory_test_db [T]()(block: ⇒ T): T = {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        AppSchema.create
      }
      block
    }
  }

  def print_AppSchema_DLL(){
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        AppSchema.printDdl
      }
    }
  }


  def running_loaded_JsonSchema [T]() (block: => T): T = {
    running_in_memory_test_db() {
      val schema = JsonSchema read_file "test/models/db/schema/json/schema.json"
      // put to db
      JsonSchema put schema
      // run block
      block
    }
  }



  /**
  * create in memory db and schema
  * independant of Play Application
  * has to be used in tests "before"
  */
  def prepare_in_mem_db() = {
    try {
      Class.forName("org.h2.Driver")
      val conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:bid3")
      SessionFactory.concreteFactory = Some(()=>
          Session.create(conn, new H2Adapter))

    }catch {
      case e => e.printStackTrace()
      false
    }
    true
  }

  /**
  * create in Postgresql db and schema
  * independant of Play Application
  */
  def prepare_postgresql_test_db() = {
    try {
      Class.forName("org.postgresql.Driver")
      val url = "jdbc:postgresql://localhost/bid_test?user=dev&password=123456"
      val conn = java.sql.DriverManager.getConnection(url)
      SessionFactory.concreteFactory = Some(()=>
          Session.create(conn, new PostgreSqlAdapter))

    }catch {
      case e => e.printStackTrace()
      false
    }
    true
  }
}


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

  def populate_test_postgres_db(json_file_name: String = "test/models/db/schema/json/schema.json") {
    prepare_postgresql_test_db()
    inTransaction {
      AppSchema.create
      val schema = JsonSchema read_file json_file_name
      put( schema )
    }
  }

}

