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
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // Campaign -* Curve relation
  lazy val campaign: ManyToOne[Campaign] = AppSchema.campaignCurves.right(this)

  // Curve -* Permutations relation
  lazy val permutations: OneToMany[Permutation] = AppSchema.curvePermutations.left(this)


  def put(): Curve = inTransaction { AppSchema.curves insert this }


  /** creates domain.Curve
  */
  // TODO: Optimize
  def domainCurve(): domain.Curve = inTransaction {
    val optimalPermutation = Permutation.get_by_id(optimalPermutation_id).headOption map(_.domainPermutation)
    domain.Curve(
      id = id,
      a = a,
      b = b,
      c = c,
      d = d,
      date = new DateTime(date),
      optimalPermutation = optimalPermutation
    )
  }


}
object Curve {


  /**
  * get Curve from DB
  * @return Curve
  */
  def get_by_id(id: Long): Campaign = inTransaction{ AppSchema.campaigns.where(a => a.id === id).single }
}
