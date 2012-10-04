import scala.collection.immutable.Range

object scrap {

                                                  
  val votes = Seq(("scala", 1), ("java", 4), ("scala", 10), ("scala", 1), ("python", 10))
                                                  //> votes  : Seq[(java.lang.String, Int)] = List((scala,1), (java,4), (scala,10)
                                                  //| , (scala,1), (python,10))
	val orderedVotes = votes
  .groupBy(_._1)
  .map { case (which, counts) =>
    (which, counts.foldLeft(0)(_ + _._2))
  }.toSeq
  .sortBy(_._2)
  .reverse                                        //> orderedVotes  : Seq[(java.lang.String, Int)] = ArrayBuffer((scala,12), (pyth
                                                  //| on,10), (java,4))
                                                  
                                                  
  val mappedByLang = votes.groupBy{ case (lang,_)=>lang }
                                                  //> mappedByLang  : scala.collection.immutable.Map[java.lang.String,Seq[(java.la
                                                  //| ng.String, Int)]] = Map(scala -> List((scala,1), (scala,10), (scala,1)), pyt
                                                  //| hon -> List((python,10)), java -> List((java,4)))
  val reducedToScore = mappedByLang.map{case (lang, seq) => (lang, seq.foldLeft(0)(_ + _._2))}
                                                  //> reducedToScore  : scala.collection.immutable.Map[java.lang.String,Int] = Map
                                                  //| (scala -> 12, python -> 10, java -> 4)
  val res = reducedToScore.toSeq.sortBy(_._2).reverse
                                                  //> res  : Seq[(java.lang.String, Int)] = ArrayBuffer((scala,12), (python,10), (
                                                  //| java,4))

  case class Tweet(val id: Long = 0, val name: String = ""){}
  val tw0 = Tweet()                               //> tw0  : scrap.Tweet = Tweet(0,)
  val tw = Tweet(1,"some")                        //> tw  : scrap.Tweet = Tweet(1,some)
  val Tweet(id, name) = tw                        //> id  : Long = 1
                                                  //| name  : String = some


}