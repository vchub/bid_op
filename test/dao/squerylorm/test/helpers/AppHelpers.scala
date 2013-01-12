package dao.squerylorm.test.helpers

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.{ H2Adapter, PostgreSqlAdapter, MySQLInnoDBAdapter }
import org.squeryl.{ Session, SessionFactory }
import play.api.test._
import play.api.test.Helpers._
import play.api.Play
import dao.squerylorm._
import scala.util.control.Exception._

trait AppHelpers {

  /**
   * creates in memory DB and schema
   * runs block of code
   * @param block of code
   * @return T
   */
  def creating_and_filling_inMemoryDB[T]()(block: => T): T = {
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
   */
  def fill_DB(): Unit

  /**
   * creates schema in mysql db.
   * which means creates relations in db/schema
   * used for prototype development phase
   */
  def create_postgreSQL_test_db_from_squeryl_schema() {
    Class.forName("org.postgresql.Driver")
    SessionFactory.concreteFactory = Some(() => Session.create(
      java.sql.DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/DBforBid_Test",
        "postgres",
        "ghfdlf"),
      new PostgreSqlAdapter))
    //Session.currentSession.cleanup
    inTransaction {      
      allCatch opt AppSchema.drop
      allCatch opt AppSchema.create
      fill_DB
    }
    /** }*/
  }

  /**
   * creates schema in default DB and fill it w/ helpers object data
   */
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
