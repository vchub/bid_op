package models.db.schema

import org.specs2.mutable._
import org.specs2.specification._

import org.squeryl.PrimitiveTypeMode.{inTransaction}

import play.api.test._
import play.api.test.Helpers._

import com.codahale.jerkson.Json
import scala.io.Source

import models.db.schema.helpers._

class TimeSlotSpec extends Specification with AllExpectations{

  "TimeSlot Entity" should {
    "be simply loaded from json" in {
        val js = """{
          "start_date" : "2012-09-19T10:00:00.000",
          "end_date" : "2012-09-19T10:30:00.000",
          "sum_search" : "2.0",
          "sum_context" : "2.5",
          "impress_search" : "4",
          "impress_context" : "5",
          "clicks_search" : "1",
          "clicks_context" : "2"
        }"""
      val timeSlot = TimeSlot.parse(js)
      timeSlot.sum_search must be_==(2.0)
    }

    "be created w/ given and default propertiess" in {
      val timeSlot = TimeSlot(sum_search = 5)
      timeSlot.sum_search must be_==(5)
    }

  }





  /*
  "method select(name: String)" should {
    sequential

    "select existing user" in {
      TestDB_0.running_FakeApplication() {
        val user = User.select(name = "Coda").head
        user.name must be_==("Coda")
    }}

    "return Nil for not existing user" in {
      //running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {}
      TestDB_0.running_FakeApplication() {
        val user = User.select(name = "coda")
        user.isEmpty must_==(true)
    }}
  }
  */



}


