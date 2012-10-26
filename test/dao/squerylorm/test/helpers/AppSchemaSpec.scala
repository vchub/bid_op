package dao.squerylorm.test.helpers

import org.specs2.mutable._
import org.specs2.specification._
import play.api.test._
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
//import org.squeryl.PrimitiveTypeMode.view2QueryAll

import dao.squerylorm._


class AppSchemaSpec extends Specification with AllExpectations {

  "create schema" should {
    sequential

    "create schema" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        inTransaction {
          AppSchema.create
          //AppSchema.printDdl
          (1===1)
        }
    }}

  }


}
