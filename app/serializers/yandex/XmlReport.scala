package serializers.yandex

import org.joda.time
//import dao.squschema._

class XmlReport(val node: xml.NodeSeq) {

  // Report date format yyyy-MM-dd
  var fmt_date = time.format.ISODateTimeFormat.date()

  /**
   * process Yandex Report
   * @param xml.NodeSeq - xml Report
   * @throw java.lang.RuntimeException if startDate, endDate or other fields are absent or malformed
   */
  def createBannerPhrasePerformanceReport: Map[domain.BannerPhrase, domain.Performance] = {
    // check dates are present and valid
    // TODO: in case the period (endDate - startDate) > 1 day make an appropriate adjustments - i.e.
    // several Performances
    val startDate: time.DateTime = fmt_date.parseDateTime((node \ "startDate").text)
    val endDate: time.DateTime = fmt_date.parseDateTime((node \ "endDate").text)

    // process rows
    val report = for (row <- (node \\ "row")) yield {
      (
        serializers.BannerPhrase(
          Some(new domain.po.Banner(network_banner_id = (row \ "@bannerID").text)),
          Some(new domain.po.Phrase(network_phrase_id = (row \ "@phrase_id").text)),
          Some(new domain.po.Region((row \ "@regionID").text))),
          serializers.Performance(
            sum_search = (row \ "@sum_search").text.toDouble,
            sum_context = (row \ "@sum_context").text.toDouble,
            impress_search = (row \ "@shows_search").text.toInt,
            impress_context = (row \ "@shows_context").text.toInt,
            clicks_search = (row \ "@clicks_search").text.toInt,
            clicks_context = (row \ "@clicks_context").text.toInt,
            start_date = startDate,
            end_date = endDate))
    }
    report.toMap
  }

}

