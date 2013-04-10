package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.lang.RuntimeException
import java.sql.Timestamp
import org.joda.time._
import scala.reflect._



@BeanInfo
case class Curve(
  val campaign_id: Long = 0, //fk
  val optimalPermutation_id: Option[Long] = None, //fk
  val a: Double = 0,
  val b: Double = 0,
  val c: Double = 0,
  val d: Double = 0,
  val date: Timestamp = new Timestamp(0)
)extends domain.Curve with KeyedEntity[Long] with History
{
  val id: Long = 0
  def dateTime = date

  /*
  def this() = this( campaign_id = 0, optimalPermutation_id = None,
                      a = 0, b = 0, c = 0, d = 0, date = new Timestamp(0))
  */

  // Campaign -* Curve relation
  lazy val campaignRel: ManyToOne[Campaign] = AppSchema.campaignCurves.right(this)


  /** select Permutation by id
  */
  def optimalPermutation = optimalPermutation_id flatMap (Permutation.get_by_id(_))


  /** save in DB
  */
  def put(): Curve = inTransaction { AppSchema.curves insert this }



}
object Curve {


  /**
  * get Curve from DB
  * @return Curve
  */
  def get_by_id(id: Long): Option[Curve] = inTransaction{ AppSchema.curves.where(a => a.id === id).headOption }


  /** Create squerylorm.Curve from domain.Curve and domain.Campaign
  */
  def apply(curve: domain.Curve, campaign: domain.Campaign): Curve = {
    // check if Permutation has an id
    val opt_perm_id: Option[Long] = curve.optimalPermutation match {
      case None => None
      case Some(perm) if(perm.id != 0) => Some(perm.id)
      // create and stores in DB new Permutation
      case Some(perm) if(perm.id == 0) => Some(Permutation.create(perm, campaign).id)
    }
    Curve(
      campaign_id = campaign.id,
      optimalPermutation_id = opt_perm_id map ((x:Long)=> x),
      a = curve.a,
      b = curve.b,
      c = curve.c,
      d = curve.d,
      date = curve.dateTime
    )
  }

  /** creates Curve record
  */
  def create(curve: domain.Curve, campaign: domain.Campaign): domain.Curve = Curve(curve, campaign).put




}
