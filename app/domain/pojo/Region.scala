package domain.pojo

import scala.reflect._
import org.joda.time._


@BeanInfo
case class Region(
  val id: Long = 0,
  val network_region_id: String = "",
  val parentRegion: Option[domain.Region] = None
)extends domain.Region
{
  def this(network_region_id: String = "") = this(0, network_region_id, None)

}
