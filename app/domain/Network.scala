package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Network(
  val id: Long,
  val name: String
){}

