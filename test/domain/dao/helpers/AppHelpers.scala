package domain.dao.helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLInnoDBAdapter}
import org.squeryl.{Session, SessionFactory}
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import domain._
import domain.dao.AppSchema

trait AppHelpers {
  /**
  * creates in memory DB and schema
  * runs block of code
  * @param block of code
  * @return T
  */
  def running_FakeApplication[T]()(block: ⇒ T): T = {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        AppSchema.create
        fill_DB
      }
      block
    }
  }

  /**
  * fills DB
  **/
  def fill_DB(): Unit

  /**
  * creates schema in mysql db.
  * which means creates relations in db/schema
  * used for prototype development phase
  **/
  def create_mysql_test_db_from_squeryl_schema() {
    running(FakeApplication()) {

      val config = Play.current.configuration
      val user = config.getString("vlad.mysql.user").get
      val psw = config.getString("vlad.mysql.psw").get
      val url = config.getString("vlad.mysql.url").get

      Class.forName("com.mysql.jdbc.Driver")
      SessionFactory.concreteFactory = Some(() => Session.create(
        java.sql.DriverManager.getConnection(url, user, psw), new MySQLInnoDBAdapter)
      )
      inTransaction {
        AppSchema.create
        AppSchema.printDdl
      }
    }
  }

  /**
  * creates schema in default DB and fill it w/ helpers object data
  **/
  def create_schema_and_fill_default_db() {
    running(FakeApplication()) {
      inTransaction {
        AppSchema.create
        AppSchema.printDdl
        fill_DB
      }
    }
  }


  def print_AppSchema_DLL(){
    running(FakeApplication()) {
      inTransaction {
        AppSchema.printDdl
      }
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
