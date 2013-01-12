package serializers

import org.joda.time.DateTime
import com.codahale.jerkson.Json

import domain._

case class BannerPhrase(
  val banner: Option[domain.Banner],
  val phrase: Option[domain.Phrase],
  val region: Option[domain.Region]) extends domain.BannerPhrase {
  @transient
  val id: Long = 0
  @transient
  var campaign: Option[domain.Campaign] = None

  @transient
  val actualBidHistory: List[ActualBidHistoryElem] = Nil
  @transient
  val recommendationHistory: List[RecommendationHistoryElem] = Nil
  @transient
  val netAdvisedBidsHistory: List[NetAdvisedBids] = Nil
  @transient
  val performanceHistory: List[Performance] = Nil
}

/**
case class Banner(
  val network_banner_id: String = "") extends domain.Banner {
  def id: Long = 0
}
/*object Banner {
  def apply(jsonString: String): Banner = {
    Json.parse[Banner](jsonString)
  }
} 
*/
case class Phrase(
  val network_phrase_id: String = "", //fk phrase_id in Network's or client's DB
  val phrase: String = "") extends domain.Phrase {
  def id: Long = 0
}
object Phrase {
  def apply(jsonString: String): Phrase = {
    Json.parse[Phrase](jsonString)
  }
}

case class Region(
  var region_id: Long = 0, //official region id
  var parent_id: Long = 0,
  val network_region_id: String = "", //fk phrase_id in Network's or client's DB
  val description: String = "") extends domain.Region {
  def id: Long = 0
  def parentRegion = None
}
object Region {
  def apply(jsonString: String): Region = {
    Json.parse[Region](jsonString)
  }
}
**/