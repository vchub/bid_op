package domain.serializing.helpers

import java.lang.RuntimeException
import org.joda.time
import domain._

trait ReportHelper {

  /**
  * process Network Reporw
  * @param xml.NodeSeq - xml Report
  * @return List[(BannerPhraseHelper, BannerPhraseStats)]
  * @throw java.lang.RuntimeException if startDate, endDate or other fields are absent or malformed
  */
  def createBannerPhraseStats(node: scala.xml.NodeSeq): Seq[ (BannerPhraseHelper, BannerPhraseStats) ]

}


object YandexXmlReportHelper extends ReportHelper {

  // Report date format yyyy-MM-dd
  val fmt_date = time.format.ISODateTimeFormat.date()

  /**
  * process Yandex Reporw
  * @param Yandex Report in xml
  * @return List[(BannerPhraseHelper, BannerPhraseStats)]
  * @throw java.lang.RuntimeException if startDate, endDate or other fields are absent or malformed
  */
  def createBannerPhraseStats(node: scala.xml.NodeSeq): Seq[ (BannerPhraseHelper, BannerPhraseStats) ] = {
    // check dates are present and valid
    val start_date: time.DateTime = fmt_date.parseDateTime((node\"startDate").text)
    val end_date: time.DateTime = fmt_date.parseDateTime((node\"endDate").text)

    // process rows
    for(row <- (node \\ "row")) yield {
      (
        BannerPhraseHelper(
          network_banner_id = (row\"@bannerID").text,
          network_phrase_id = (row\"@phrase_id").text,
          network_region_id = (row\"@regionID").text,
          bid = 0 //TODO
        ),
        BannerPhraseStats(
          sum_search = (row\"@sum_search").text.toDouble,
          sum_context = (row\"@sum_context").text.toDouble,
          impress_search = (row\"@shows_search").text.toInt,
          impress_context = (row\"@shows_context").text.toInt,
          clicks_search = (row\"@clicks_search").text.toInt,
          clicks_context = (row\"@clicks_context").text.toInt,
          start_date = start_date.toDate,
          end_date = end_date.toDate
        )
      )
    }
  }
}

