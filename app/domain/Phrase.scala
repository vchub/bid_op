package domain

import scala.reflect._


@BeanInfo
case class Phrase(
  val id: Long = 0,
  val phrase: String = ""
){}

