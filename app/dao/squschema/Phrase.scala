package dao.squschema

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import scala.reflect._


@BeanInfo
case class Phrase(
  val network_phrase_id: String = "", //fk phrase_id in Network's or client's DB
  val phrase: String = ""
)extends KeyedEntity[Long]
{
  val id: Long = 0

  // Phrase -* BannerPhrase relation
  lazy val bannerPhrases: OneToMany[BannerPhrase] = AppSchema.phraseBannerPhrases.left(this)



  /**
  * default put - save to db
  * @param
  * @return Phrase
  **/
  def put(): Phrase = inTransaction { AppSchema.phrases insert this }
}

object Phrase {

  /**
  * select by network_phrase_id
  * @param String
  * @return List[Phrase]
  */
  def select(network_phrase_id: String): List[Phrase] = inTransaction{ AppSchema.phrases.where(a => a.network_phrase_id === network_phrase_id).toList}//.single }
}

