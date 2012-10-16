package domain

import scala.reflect._


@BeanInfo
case class Phrase(
  val id: Long,
  val network_phrase_id: String,
  val phrase: String
){}

