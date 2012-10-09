package domain

import scala.reflect._


@BeanInfo
case class User(
  val id: Long = 0,
  val name: String = ""
){}

