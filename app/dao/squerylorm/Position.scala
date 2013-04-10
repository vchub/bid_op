package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._
import common._

@BeanInfo
case class Position(
  val bannerphrase_id: Long = 0, //fk
  val permutation_id: Long = 0, //fk
  val position: Int = 0) extends domain.Position with KeyedEntity[Long] {
  val id: Long = 0

  // BannerPhrase -* Position relation
  lazy val bannerPhraseRel: ManyToOne[BannerPhrase] = AppSchema.bannerPhrasePositions.right(this)

  // Permutation -* Position relation
  lazy val permutationRel: ManyToOne[Permutation] = AppSchema.permutationPositions.right(this)

  /**
   * default put - save to db
   */
  def put(): Position = inTransaction { AppSchema.positions insert this }

}

/**
object Position {
  def apply(pos: domain.Position, bp: domain.BannerPhrase, perm: domain.Permutation): Position = {
    Position(
      bannerphrase_id = bp.id,
      permutation_id = perm.id,
      position = pos.position)
  }

  def create(pos: domain.Position, bp: domain.BannerPhrase, perm: domain.Permutation): domain.Position =
    Position(pos, bp, perm).put
}
**/
