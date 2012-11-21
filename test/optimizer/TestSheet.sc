package optimizer

//import scala.util.Random
import dao.squerylorm._
import dao.squerylorm.test.helpers._
import java.util.Random
import sun.security.util.Length
import org.squeryl.PrimitiveTypeMode.inTransaction

object TestSheet {
  val bannerPhrases = (0 until 2 map
    (i => (0 until 3 map
      (j => (i, j)))))                            //> bannerPhrases  : scala.collection.immutable.IndexedSeq[scala.collection.immu
                                                  //| table.IndexedSeq[(Int, Int)]] = Vector(Vector((0,0), (0,1), (0,2)), Vector((
                                                  //| 1,0), (1,1), (1,2)))

  val v1 = for (i <- (0 until 2); j <- (0 until 3)) yield (i, j)
                                                  //> v1  : scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((0,0), (0,1
                                                  //| ), (0,2), (1,0), (1,1), (1,2))

  val r = new Random()                            //> r  : java.util.Random = java.util.Random@31828889

  val r1 = 1 to 1 map (_ => 1 + r.nextGaussian() / 10)
                                                  //> r1  : scala.collection.immutable.IndexedSeq[Double] = Vector(0.9185587231903
                                                  //| 134)
  val r2 = 1 to 1 map (_ => r.nextInt(1))         //> r2  : scala.collection.immutable.IndexedSeq[Int] = Vector(0)

  val db = TestDB_1.creating_and_filling_inMemoryDB() {
    inTransaction {
      import org.squeryl.PrimitiveTypeMode._
      val user = User.select(name = "User_0").head
      val network = Network.select("Network_0").head
      val campaign = Campaign.select(user.name, network.name)(0)
      
      (AppSchema.campaigns.toList.head.network_campaign_id,
      AppSchema.users.toList.head.name,
      AppSchema.users.toList.head.id,
      AppSchema.campaigns.toList.head.user_id,
      AppSchema.networks.toList.head.name,
      AppSchema.networks.toList.head.id,
      AppSchema.campaigns.toList.head.network_id,
      AppSchema.regions.toList,
      AppSchema.banners.toList,
      AppSchema.phrases.toList,
      AppSchema.bannerphrases.toList,
      AppSchema.curves.toList,
      AppSchema.permutations.toList.take(50).last.campaign_id,
      AppSchema.positions.toList)
      
      //campaign.bannerPhrasesRel.toList.groupBy(_.banner.get.network_banner_id).size)
    }
    
    // Campaign.select("User_0", "Network_0", "Net_0_id").head //must_!=(Nil)
  }                                               //> db  : (String, String, Long, Long, String, Long, Long, List[dao.squerylorm.
                                                  //| Region], List[dao.squerylorm.Banner], List[dao.squerylorm.Phrase], List[dao
                                                  //| .squerylorm.BannerPhrase], List[dao.squerylorm.Curve], Long, List[dao.squer
                                                  //| ylorm.Position]) = (Net_0_Id,User_0,1,1,Network_0,1,1,List(Region(7,0,7,Rus
                                                  //| sia)),List(Banner(Banner_0), Banner(Banner_1), Banner(Banner_2), Banner(Ban
                                                  //| ner_3), Banner(Banner_4)),List(Phrase(0,Phrase_00), Phrase(1,Phrase_01), Ph
                                                  //| rase(2,Phrase_02), Phrase(3,Phrase_03), Phrase(4,Phrase_04), Phrase(5,Phras
                                                  //| e_05), Phrase(6,Phrase_06), Phrase(7,Phrase_07), Phrase(8,Phrase_08), Phras
                                                  //| e(9,Phrase_09), Phrase(10,Phrase_10), Phrase(11,Phrase_11), Phrase(12,Phras
                                                  //| e_12), Phrase(13,Phrase_13), Phrase(14,Phrase_14), Phrase(15,Phrase_15), Ph
                                                  //| rase(16,Phrase_16), Phrase(17,Phrase_17), Phrase(18,Phrase_18), Phrase(19,P
                                                  //| hrase_19), Phrase(20,Phrase_20), Phrase(21,Phrase_21), Phrase(22,Phrase_22)
                                                  //| , Phrase(23,Phrase_23),
                                                  //| Output exceeds cutoff limit.
}