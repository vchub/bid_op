package serializers.yandex

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.lang.RuntimeException
import play.api.test._
import play.api.test.Helpers._


class XmlReportSpec extends FlatSpec with ShouldMatchers{

  "createBannerPhraseStats" should
    "throwgh an Error if start_date or end_date is missed" in {
      // get test data
      val file_name = "test/serializers/yandex/reports/report1.xml"
      val node = xml.XML.loadFile(file_name)
      // create a copy w/o end_date
      val stat = <report> {(node\"stat")}</report>

      // just checking - not Exceptions
      (new XmlReport(node)).createBannerPhrasePerformanceReport
      // but w/ no dates
      intercept[RuntimeException] {
        (new XmlReport(stat)).createBannerPhrasePerformanceReport
      }
    }

    it should "create Map[BannerPhrase, Performance]" in {
      // get test data
      val file_name = "test/serializers/yandex/reports/report1.xml"
      val node = scala.xml.XML.loadFile(file_name)

      val res = (new XmlReport(node)).createBannerPhrasePerformanceReport
      res.size should be (3)
      res.keySet.exists(x=>x.banner.get.network_banner_id == "123456") should be (true)
    }
}


