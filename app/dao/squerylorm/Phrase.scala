package dao.squerylorm

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._


@BeanInfo
case class Phrase(
  val network_phrase_id: String = "", //fk phrase_id in Network's or client's DB
  val phrase: String = ""
)extends domain.Phrase with KeyedEntity[Long]
{
  val id: Long = 0

  // Phrase -* BannerPhrase relation
  lazy val bannerPhrasesRel: OneToMany[BannerPhrase] = AppSchema.phraseBannerPhrases.left(this)



  /**
  * default put - save to db
  **/
  def put(): Phrase = inTransaction { AppSchema.phrases insert this }

}

object Phrase {

  /** construct Phrase from domain.Phrase
  */
  def apply(p: domain.Phrase): Phrase =
    Phrase(
      network_phrase_id = p.network_phrase_id,
      phrase = p.phrase
    )


  /**
  * select by Campaing and domain.Phrase (basically network_phrase_id)
  * TODO: now it's simply wrong. it has to check BP-B-Campaing association
  */
  def select(p: domain.Phrase): Option[Phrase] = inTransaction{
    AppSchema.phrases.where(a =>
      a.network_phrase_id === p.network_phrase_id
    ).headOption
  }


}

