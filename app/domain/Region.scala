package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Region(
  val id: Long,
  parentRegion: Region
){}
