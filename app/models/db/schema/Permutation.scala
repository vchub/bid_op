package models.db
package schema

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

import scala.reflect._

import common._



@BeanInfo
case class Permutation(
  val timeslot_id: Long = 0, //fk
  val bannerphrase_id: Long = 0, //fk
  val position: Int = 0,
  val bid: Double = 0   // recommended bid
                        //although a bid can be calculated through the position
                        // we rather have an absolute number
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // BannerPhrase -* Permutation relation
  lazy val bannerPhrase: ManyToOne[BannerPhrase] = AppSchema.bannerPhrasePermutatons.right(this)

  // TimeSlot -* Permutation relation
  lazy val timeslot: ManyToOne[TimeSlot] = AppSchema.timeSlotPermutation.right(this)



  /**
  * default put - save to db
  * @param
  * @return Permutation
  **/
  def put(): Permutation = inTransaction { AppSchema.permutations insert this }

}


