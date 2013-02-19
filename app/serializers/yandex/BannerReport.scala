package serializers.yandex

import org.joda.time._

import play.api.libs.json._

import common._

import domain._

/** Yandex GetBanners (Live) API call output data structure */
case class BannerReport(
  val data: List[BannerInfo]) { //,
  //@transient val trash: String = "") {

  /** creates Map (see return) of domain.Types */
  def getDomainReport: Map[BannerPhrase, (ActualBidHistoryElem, NetAdvisedBids)] = {
    val res = for {
      bInfo <- data
      region <- bInfo.createRegions()
      phInfo <- bInfo.Phrases
    } yield {
      (
        serializers.BannerPhrase(
          banner = Some(domain.po.Banner(network_banner_id = phInfo.BannerID.toString)),
          phrase = Some(domain.po.Phrase(network_phrase_id = phInfo.PhraseID.toString, phrase = phInfo.Phrase)),
          region = Some(region)),
          (new ActualBidHistoryElem { val dateTime = new DateTime; val elem = phInfo.Price },
            po.NetAdvisedBids(
              a = phInfo.Min,
              b = phInfo.Max,
              c = phInfo.PremiumMin,
              d = phInfo.PremiumMax,
              e = phInfo.CurrentOnSearch,
              f = 0,
              dateTime = new DateTime)))
    }
    res toMap
  }

}

object BannerReport extends Function1[List[BannerInfo], BannerReport] {
  def _apply(jsValue: JsValue): BannerReport = {
    Json.fromJson[BannerReport](jsValue)(json_api.Reads.bannerReport).get
    /*import common.Reads.bannerReport
    jsValue.validate[BannerReport].map { br => br }.
      recoverTotal(er => { println(er); BannerReport(data = List()) })*/
  }
}

case class BannerInfo(
  val BannerID: Long,
  val Text: String,
  /**String containing a comma-separated list of IDs for the ad display regions. An ID of 0 or an empty string indicate displays in all regions.*/
  val Geo: String,
  val Phrases: List[BannerPhraseInfo]) {
  /** create domain.Regions from Geo: String */
  def createRegions(geo: String = Geo): Seq[Region] =
    if (geo.trim.isEmpty) List(new po.Region("0"))
    else
      geo.split(',') map (x => new po.Region(x.trim.toString))

}

case class BannerPhraseInfo(
  val BannerID: Long,
  val PhraseID: Long,
  val CampaignID: Long,
  val Phrase: String,
  val Min: Double,
  val Max: Double,
  val PremiumMin: Double,
  val PremiumMax: Double,
  // val ContextPrice: Double, //CPC on sites in the Yandex Advertising Network
  val AutoBroker: String, // Yes / No
  val Price: Double, // Maximum CPC on Yandex search set for the phrase.
  val CurrentOnSearch: Double //The current CPC set by Autobroker
  ) {}

