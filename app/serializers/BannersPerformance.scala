package serializers

import org.joda.time._

object BannersPerformance {

  // Report date format yyyy-MM-dd
  val fmt_date = format.ISODateTimeFormat.date()

  def createBannerPhrasePerformanceReport(bsr: GetBannersStatResponse, c: dao.squerylorm.Campaign, cur_date: DateTime): Map[domain.BannerPhrase, domain.Performance] = {
    // check dates are present and valid
    // TODO: in case the period (endDate - startDate) > 1 day make an appropriate adjustments - i.e.
    // several Performances
    val startDate: DateTime = fmt_date.parseDateTime(bsr.StartDate)
    val endDate: DateTime = cur_date
    
    // process rows
    val report = for (bsi <- bsr.Stat) yield {
      val phID = bsi.PhraseID.map(_.toString).getOrElse { //if yandex return null for phraseID
        dao.squerylorm.Phrase.select(bsi.Phrase).map(_.network_phrase_id).getOrElse("")
      }
      (serializers.BannerPhrase(
        Some(new domain.po.Banner(network_banner_id = bsi.BannerID.toString)),
        Some(new domain.po.Phrase(network_phrase_id = phID)),
        Some(new domain.po.Region("0"))), //"0" is an indicator of all regions
        serializers.Performance(
          sum_search = bsi.SumSearch,
          sum_context = bsi.SumContext,
          impress_search = bsi.ShowsSearch,
          impress_context = bsi.ShowsContext,
          clicks_search = bsi.ClicksSearch,
          clicks_context = bsi.ClicksContext,
          start_date = startDate,
          end_date = endDate))
    }
    report.toMap
  }

}

case class GetBannersStatResponse(
  val CampaignID: Int,
  val StartDate: String, //Date
  val EndDate: String, //Date
  val Stat: List[BannersStatItem])

case class BannersStatItem(
  //val StatDate: DateTime = new DateTime,
  val BannerID: Int = 0,
  val PhraseID: Option[Int] = Some(0),
  //val RubricID: Int = 0,
  val Phrase: String = "",
  val Sum: Double = 0,
  val SumSearch: Double = 0,
  val SumContext: Double = 0,
  val Clicks: Int = 0,
  val ClicksSearch: Int = 0,
  val ClicksContext: Int = 0,
  val Shows: Int = 0,
  val ShowsSearch: Int = 0,
  val ShowsContext: Int = 0)