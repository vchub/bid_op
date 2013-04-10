package domain

trait Region{
  def id: Long
  def network_region_id: String
  def parentRegion: Option[Region]
}
