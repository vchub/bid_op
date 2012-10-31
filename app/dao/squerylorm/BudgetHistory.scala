package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.joda.time._
import java.sql.Timestamp
import scala.reflect._
import common._



@BeanInfo
case class BudgetHistory(
  val campaign_id: Long = 0, //fk
  val date: Timestamp = new Timestamp(0),
  val budget: Double = 0
)extends domain.BudgetHistoryElem with KeyedEntity[Long] with History
{
  val id: Long = 0
  def elem = budget
  def dateTime = date

  /**
  * default put - save to db
  **/
  def put(): BudgetHistory = inTransaction { AppSchema.budgethistory insert this }



}


