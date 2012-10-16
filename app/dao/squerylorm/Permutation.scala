package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import java.util.Date
import org.joda.time._
import scala.reflect._
import common._



@BeanInfo
case class Permutation(
  val curve_id: Long = 0, //fk
  val date: Date = new Date()
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // Curve -* Permutation relation
  lazy val curve: ManyToOne[Curve] = AppSchema.curvePermutations.right(this)

  // Permutation -* Position relation
  lazy val positions: OneToMany[Position] = AppSchema.permutationPositions.left(this)

  /** default put - save to db
  */
  def put(): Permutation = inTransaction { AppSchema.permutations insert this }


  /** creates domain.Permutation
  */
  // TODO: Optimize
  def domainPermutation(): domain.Permutation = inTransaction {
    // get positions
    val positions = this.positions.toList map ((x: Position ) =>
      //TODO: if(! x.bannerPhrase.isEmpty)
      (
        x.bannerPhrase.head.domainBannerPhrase,
        x.domainPosition
      )
    )

    val permutation = positions.toMap

    domain.Permutation(
      date = new DateTime(date),
      permutation = permutation
    )
  }

}

object Permutation{

  /** get Permutation from DB
  */
  def get_by_id(id: Long) = inTransaction{ AppSchema.permutations.where(a => a.id === id)}
}


