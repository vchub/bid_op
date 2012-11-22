package optimizer

import domain._
import org.joda.time._
import org.joda.time.DateTime

class Optimizer(_t: DateTime, _c: Campaign) {
  val c = _c
  val t = _t

  //c.performanceHistory.tail.head.dateTime
  //c.performanceHistory.head.cost_context + c.performanceHistory.head.cost_search

  /**create Permutations, local and optimal respectively -------------------------------------------------*/
  def createLocalPermutation: Permutation = new domain.po.Permutation(0, dateTime = t, permutation = createLocalPermutationMap(10)._1)
  def createOptimalPermutation: Permutation = new domain.po.Permutation(0, dateTime = t, permutation = createOptimalPermutationMap(48))

  /** createLocalPermutationMap -----------------------------------------------------------------------*/
  def createLocalPermutationMap(NumberOfSegments: Int): (Map[BannerPhrase, Position], List[Int]) = {
    //calculate the number of element in one segments, where NumberOfSegments is the number of segments
    val n = c.bannerPhrases.length / NumberOfSegments

    /** Creation of local position permutations by shuffling the elements in each of n segments*/
    def perm(ipPerm: List[(Int, domain.Position)]): List[(Int, domain.Position)] =
      if (ipPerm.length <= n) scala.util.Random.shuffle(ipPerm)
      else scala.util.Random.shuffle(ipPerm.take(n)) ::: perm(ipPerm.drop(n))

    //get optimal permutation from the current curve
    val optPerm = c.curves.head.optimalPermutation.head.permutation

    //get Positions
    val OptPos = optPerm.toList map { case (bp, p) => p }

    //create local position permutation List[(Int, domain.Position)]
    //Int corresponds to permutation of indices 1..N, domain.Position corresponds to permutation of positions
    val ipPerm = perm(((0 until OptPos.length) zip OptPos).toList)

    val pPerm = ipPerm map { case (ind, pos) => pos }
    val iPerm = ipPerm map { case (ind, pos) => ind }

    //create local permutation map
    ((c.bannerPhrases zip pPerm).toMap, iPerm)
  }

  /** createOptimalPermutationMap -----------------------------------------------------------------------*/
  def createOptimalPermutationMap(NumOfTimestamps: Int): Map[BannerPhrase, Position] = {

    val cost_List = c.performanceHistory.take(NumOfTimestamps) map
      (perf => perf.cost_context + perf.cost_search)

    val perm_List = c.permutationHistory.take(NumOfTimestamps)

    //Define compare function s(i,j) for bannerPhrases with id=i and id=j.
    def s(bp_i: BannerPhrase, bp_j: BannerPhrase): Double = {
      val perm_i = perm_List map {
        perm => perm.permutation.get(bp_i)
      }
      val perm_j = perm_List map {
        perm => perm.permutation.get(bp_j)
      }
      val fullList = cost_List zip (perm_i zip perm_j)
      val s_plus_List = fullList filter { case (c, (pi, pj)) => pi.get.position < pj.get.position }
      val s_minus_List = fullList filter { case (c, (pi, pj)) => pi.get.position > pj.get.position }

      s_plus_List.map(_._1).sum - s_minus_List.map(_._1).sum
    }

    def S(bp_i: BannerPhrase): Double = {
      val s_List = c.bannerPhrases map (bp_j => s(bp_i, bp_j))
      s_List.sum / s_List.length
    }

    val _S = c.bannerPhrases map (bp_i => (bp_i, S(bp_i)))

    val res = (0 until c.bannerPhrases.length zip (_S sortWith (_._2 < _._2))).toList

    (res map {
      case (ind, (bp, s)) => (bp, new domain.po.Position(ind))
    }).toMap

  }

  /** createCurve --------------------------------------------------------------------------------------*/
  def createCurve: Curve = new domain.po.Curve(0, 1, 1, 1, 1, t, None)

  //estimate ScaleParameter

  //some crutch
  //budget per day
  //tF(t) - traffic function
  //bF(t) - budget function

  //val B_day = 1
  //def tF(t: DateTime): Double = t.getMinuteOfDay()
  //def bF(t: DateTime): Double = tF(t) / B_day

  //i think to define all in Campaign
  //and then use just the functions:
  //get_tF(t:DateTime)
  //get_bF(t:DateTime)

  /** getScaleParameter-----------------------------------------------------------------------------------*/
  def getScaleParameter(ScaleParameter: Double): Double = 1
  //val delta_B = (bF(t0) - cost(t0)) * (T - t1) / (T - t0) - (bF(t1) - cost(t1))
  //ScaleParameter(1+delta_B/cost(t1))

}