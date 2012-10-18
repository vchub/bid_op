package dao.squerylorm.test.helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLInnoDBAdapter}
import org.squeryl.{Session, SessionFactory}
import play.api.test._
import play.api.test.Helpers._
import play.api.Play

import dao.squerylorm._

trait AppHelpers {

  /**
  * creates in memory DB and schema
  * runs block of code
  * @param block of code
  * @return T
  */
  def creating_and_filling_inMemoryDB[T]()(block: ⇒ T): T = {
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


  /**
  * creates and and fills schema in default DB
  * doesn't use running(FakeApplication()){T}
  * Used for Java and Junit testing.
  */
  def create_schema_fill_default_DB = inTransaction {
        AppSchema.create
        fill_DB
  }

}
