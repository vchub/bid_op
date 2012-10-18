package optimizer.daosample

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._

// application TestDB_0 Helper
import dao.squerylorm.test.helpers.TestDB_0

case class TestDBHelper(val f: Runnable) extends Runnable {
  def run = {
    inTransaction {
      TestDB_0.create_schema_fill_default_DB
      //run the "callback"
      f.run()
    }
  }
}

/*
case class TestDBHelper(val f: Runnable) extends Runnable {
  def run = {
    inTransaction {
      //create in memory test DB
      //AppSchema.create
      //run the "callback"
      //f.run()
    }
  }

}
*/


