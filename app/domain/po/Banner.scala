package domain.po

import scala.reflect._


@BeanInfo
case class Banner(
  val id: Long = 0,
  val network_banner_id: String
) extends domain.Banner {}
