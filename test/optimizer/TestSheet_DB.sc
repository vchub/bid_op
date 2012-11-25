package optimizer

import dao.squerylorm._
import dao.squerylorm.test.helpers._
import scala.util.Random
import sun.security.util.Length
import org.squeryl.PrimitiveTypeMode.inTransaction
import scala.collection.Map
import org.joda.time._

object TestSheet_DB {
 
	val dao = new SquerylDao                  //> dao  : dao.squerylorm.SquerylDao = dao.squerylorm.SquerylDao@792e76db
	val opt = new Optimizer                   //> opt  : optimizer.Optimizer = optimizer.Optimizer@67e89f1b
  val midnnight_formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd")
                                                  //> midnnight_formatter  : org.joda.time.format.DateTimeFormatter = org.joda.tim
                                                  //| e.format.DateTimeFormatter@34780c0e
  //set startDate and endDate
  val dateStart = midnnight_formatter.parseDateTime("2012-09-19")
                                                  //> dateStart  : org.joda.time.DateTime = 2012-09-19T00:00:00.000+04:00
  val dateEnd = dateStart.plusMinutes(30 * (48 * 10 + 24))
                                                  //> dateEnd  : org.joda.time.DateTime = 2012-09-29T12:00:00.000+04:00
 
  val db = TestDB_1.creating_and_filling_inMemoryDB() {
    inTransaction {
      val campaign = dao.getCampaign("User_0", "Network_0", "Net_0_Id", dateStart, dateEnd).get
      //val user = User.select(name = "Coda").head
      //val network = Network.select("Yandex").head
      //val (permutation, ind) = opt.createLocalPermutationMap(campaign,5)
      val permutation = opt.createOptimalPermutationMap(campaign,48)
                 
      //(opt.PermutationMeasure(ind),
      (permutation map {case (bp,p)=>p.position},
      (campaign.permutationHistory(0).permutation map {case (bp,p)=>p.position}).toList.sort(_<_),
      campaign.permutationHistory(1).permutation map {case (bp,p)=>p.position},
      campaign.bannerPhrases.length,
      campaign.curves.head.optimalPermutation,
      campaign.permutationHistory.head.id,
      campaign.budget.get,
      campaign.budgetHistory,
      campaign.historyStartDate,campaign.historyEndDate,
      campaign.permutationHistory.head,
      campaign.network_campaign_id,
      campaign.user_id,
      campaign.network_id,
      campaign.bannerPhrases.head.region.get,
      campaign.bannerPhrases.head.banner.get,
      campaign.bannerPhrases.head.phrase.get,
      campaign.permutationHistory.head.permutation.get(campaign.bannerPhrases.head).get,
      campaign.bannerPhrases.head
      )._1
      
      
      
      //campaign.bannerPhrasesRel.toList.groupBy(_.banner.get.network_banner_id).size)
    }
}                                                 //> db  : scala.collection.immutable.Iterable[Int] = List(10, 32, 42, 12, 1, 49
                                                  //| , 30, 48, 11, 3, 0, 40, 13, 33, 8, 9, 43, 47, 34, 21, 26, 5, 28, 35, 23, 31
                                                  //| , 44, 6, 7, 27, 19, 2, 25, 45, 18, 41, 37, 46, 4, 29, 36, 17, 38, 16, 20, 1
                                                  //| 4, 24, 15, 39, 22)
}