package domain

import scala.reflect._


@BeanInfo
case class User(
  val id: Long,
  val name: String
){}

