package dao.squerylorm

import org.specs2.mutable._
import org.specs2.specification._

import org.joda.time._
import java.sql.Timestamp
import scala.xml._

import dao.squerylorm.test.helpers._

class CheckTimeSpec extends Specification with AllExpectations {

  "check if timestapm is stored." should {
    sequential
    "create, store and retrieve" in {
      TestDB_0.creating_and_filling_inMemoryDB() {
        //val dao = new SquerylDao
        val dateTime = new DateTime
        val ct = CheckTime(
          date = new Timestamp(dateTime.getMillis),
          dateDate = dateTime.toDate,
          elem = 1.0).put
        val res_ct = CheckTime.get_by_id(ct.id)
        new DateTime(res_ct.get.date) must_==(dateTime)
        new DateTime(res_ct.get.dateDate) must_!=(dateTime)
    }}
  }


}


