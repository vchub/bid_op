package optimizer

// application db
import dao.squschema.AppSchema

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._


case class TestDBHelper(val f: Runnable) extends Runnable {
  def run = {
    inTransaction {
      //create in memory test DB
      AppSchema.create
      //run the "callback"
      f.run()
    }
  }

}


