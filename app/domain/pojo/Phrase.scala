package domain.pojo

import scala.reflect._


@BeanInfo
case class Phrase(
  val id: Long = 0,
  val network_phrase_id: String,
  val phrase: String = ""
) extends domain.Phrase {}

