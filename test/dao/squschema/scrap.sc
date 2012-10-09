import scala.collection.immutable.Range

object scrap {

                                                  
  val votes = Seq(("scala", 1), ("java", 4), ("scala", 10), ("scala", 1), ("python", 10))
	val orderedVotes = votes
  .groupBy(_._1)
  .map { case (which, counts) =>
    (which, counts.foldLeft(0)(_ + _._2))
  }.toSeq
  .sortBy(_._2)
  .reverse
                                                  
                                                  
  val mappedByLang = votes.groupBy{ case (lang,_)=>lang }
  val reducedToScore = mappedByLang.map{case (lang, seq) => (lang, seq.foldLeft(0)(_ + _._2))}
  val res = reducedToScore.toSeq.sortBy(_._2).reverse

  case class Tweet(val id: Long = 0, val name: String = ""){}
  val tw0 = Tweet()
  val tw = Tweet(1,"some")
  val Tweet(id, name) = tw


}