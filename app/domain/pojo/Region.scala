package domain.pojo

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Region(
  val id: Long,
  val parentRegion: Option[Region]
)extends domain.Region {}
