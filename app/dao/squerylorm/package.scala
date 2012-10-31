package dao

package object squerylorm{

  import org.squeryl._
  import org.squeryl.PrimitiveTypeMode._
  import org.squeryl.dsl._

  import org.joda.time.DateTime
  import java.sql.Timestamp


  implicit def convertFromJdbc(t: Timestamp) = new DateTime(t)
  implicit def convertToJdbc(t: DateTime) = new Timestamp(t.getMillis())

  trait History{
    def date: Timestamp
  }






}

