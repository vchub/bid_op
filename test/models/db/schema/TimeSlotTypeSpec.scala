package models.db.schema

import org.specs2.mutable._
import org.specs2.specification._

import java.util.Date
import java.text.SimpleDateFormat

import play.api.test._
import play.api.test.Helpers._


class TimeSlotTypeSpec extends Specification with AllExpectations {

  "get_timeslottype" should {

    "be DI through AppSchemaFactory" in {
      // inject mock TimeSlotType.get_timeslottypeo
      class MockFactory extends AppSchemaFactory {
        override def get_TimeSlotType() = {
          val o = new TimeSlotType("")
          o.id = 1
          o
        }
      }

      val t0 = new TimeSlotType("")
      t0.id must_==(0)

      val factory = new MockFactory
      val res = t0.some(1) (factory.get_TimeSlotType)
      res.id must_==(2)
    }

    "return 1 for working days" in {
      val start_date = common.Helpers.get_Date_from_standart_string("2012-09-20 10:00:00")
      val end_date = common.Helpers.get_Date_from_standart_string("2012-09-20 10:30:00")
      val tst = (new AppSchemaFactory).get_TimeSlotType
      val res = tst.get_timeslottype(start_date, end_date)
      res.id must_==(1)
    }

    "return 2 for weedend days" in {
      val start_date = common.Helpers.get_Date_from_standart_string("2012-09-23 10:00:00")
      val end_date = common.Helpers.get_Date_from_standart_string("2012-09-23 10:30:00")
      val tst = (new AppSchemaFactory).get_TimeSlotType
      val res = tst.get_timeslottype(start_date, end_date)
      res.id must_==(2)
    }
  }
}
