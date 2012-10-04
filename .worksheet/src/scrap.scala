import scala.collection.immutable.Range

object scrap {import scala.runtime.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(197); 

                                                  
  val votes = Seq(("scala", 1), ("java", 4), ("scala", 10), ("scala", 1), ("python", 10));System.out.println("""votes  : Seq[(java.lang.String, Int)] = """ + $show(votes ));$skip(155); 
	val orderedVotes = votes
  .groupBy(_._1)
  .map { case (which, counts) =>
    (which, counts.foldLeft(0)(_ + _._2))
  }.toSeq
  .sortBy(_._2)
  .reverse;System.out.println("""orderedVotes  : Seq[(java.lang.String, Int)] = """ + $show(orderedVotes ));$skip(160); 
                                                  
                                                  
  val mappedByLang = votes.groupBy{ case (lang,_)=>lang };System.out.println("""mappedByLang  : scala.collection.immutable.Map[java.lang.String,Seq[(java.lang.String, Int)]] = """ + $show(mappedByLang ));$skip(95); 
  val reducedToScore = mappedByLang.map{case (lang, seq) => (lang, seq.foldLeft(0)(_ + _._2))};System.out.println("""reducedToScore  : scala.collection.immutable.Map[java.lang.String,Int] = """ + $show(reducedToScore ));$skip(54); 
  val res = reducedToScore.toSeq.sortBy(_._2).reverse

  case class Tweet(val id: Long = 0, val name: String = ""){};System.out.println("""res  : Seq[(java.lang.String, Int)] = """ + $show(res ));$skip(83); 
  val tw0 = Tweet();System.out.println("""tw0  : scrap.Tweet = """ + $show(tw0 ));$skip(27); 
  val tw = Tweet(1,"some");System.out.println("""tw  : scrap.Tweet = """ + $show(tw ));$skip(27); 
  val Tweet(id, name) = tw;System.out.println("""id  : Long = """ + $show(id ));System.out.println("""name  : String = """ + $show(name ))}


}