package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.lang.RuntimeException
import java.util.Date
import org.joda.time._
import scala.reflect._



@BeanInfo
case class Curve(
  val campaign_id: Long = 0, //fk
  val optimalPermutation_id: Long = 0, //fk
  val a: Double = 0,
  val b: Double = 0,
  val c: Double = 0,
  val d: Double = 0,
  val date: Date = new Date()
)extends domain.Curve with KeyedEntity[Long]
{
  val id: Long = 0
  def dateTime = new DateTime(date)

  // Campaign -* Curve relation
  lazy val campaignRel: ManyToOne[Campaign] = AppSchema.campaignCurves.right(this)


  /** select Permutation by id
  */
  def optimalPermutation = Permutation.get_by_id(optimalPermutation_id)


  def put(): Curve = inTransaction { AppSchema.curves insert this }



}
object Curve {


  /**
  * get Curve from DB
  * @return Curve
  */
  def get_by_id(id: Long): Campaign = inTransaction{ AppSchema.campaigns.where(a => a.id === id).single }
}
