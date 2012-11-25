package optimizer

import domain._
import org.joda.time._

class Optimizer {

  //Daily minute time interval: [0,T] 
  val T = org.joda.time.DateTimeConstants.MINUTES_PER_DAY

  /**create Permutations, local and optimal respectively -------------------------------------------------*/
  def createLocalPermutation(c: Campaign, t: DateTime, NumberOfSegments: Int = 10): Permutation =
    new domain.po.Permutation(
      id = 0,
      dateTime = t,
      permutation = createLocalPermutationMap(campaign = c, NumberOfSegments = NumberOfSegments)._1)

  def createOptimalPermutation(c: Campaign, t: DateTime, NumOfTimestamps: Int = 48): Permutation =
    new domain.po.Permutation(
      id = 0,
      dateTime = t,
      permutation = createOptimalPermutationMap(campaign = c, NumOfTimestamps = NumOfTimestamps))

  /** createLocalPermutationMap -----------------------------------------------------------------------*/
  def createLocalPermutationMap(campaign: Campaign, NumberOfSegments: Int = 10): (Map[BannerPhrase, Position], List[Int]) = {
    //calculate the number of element in one segments, where NumberOfSegments is the number of segments
    val n = campaign.bannerPhrases.length / NumberOfSegments

    /** Creation of local position permutations by shuffling the elements in each of n segments*/
    def perm(ipPerm: List[(Int, domain.Position)]): List[(Int, domain.Position)] =
      if (ipPerm.length <= n) scala.util.Random.shuffle(ipPerm)
      else scala.util.Random.shuffle(ipPerm.take(n)) ::: perm(ipPerm.drop(n))

    //get optimal permutation from the current curve
    val optPerm = campaign.curves.head.optimalPermutation.get.permutation

    //get Positions
    val OptPos = optPerm.toList map { case (bp, p) => p }

    //create local position permutation List[(Int, domain.Position)]
    //Int corresponds to permutation of indices 1..N, domain.Position corresponds to permutation of positions
    val ipPerm = perm(((0 until OptPos.length) zip OptPos).toList)

    val pPerm = ipPerm map { case (ind, pos) => pos }
    val iPerm = ipPerm map { case (ind, pos) => ind }

    //create local permutation map
    ((campaign.bannerPhrases zip pPerm).toMap, iPerm)
  }

  /** createOptimalPermutationMap -----------------------------------------------------------------------*/
  def createOptimalPermutationMap(campaign: Campaign, NumOfTimestamps: Int = 48): Map[BannerPhrase, Position] = {

    val cost_List = campaign.performanceHistory.take(NumOfTimestamps) map
      (perf => cost(perf))

    val perm_List = campaign.permutationHistory.take(NumOfTimestamps)

    //Define compare function s(i,j) for bannerPhrases with id=i and id=j.
    def s(bp_i: BannerPhrase, bp_j: BannerPhrase): Double = {
      val sij_List = (0 until cost_List.length map {
        k =>
          {
            if (perm_List(k).permutation.get(bp_i).get.position < perm_List(k).permutation.get(bp_j).get.position)
              cost_List(k)
            else -cost_List(k)
          }
      }).toList
      sij_List.sum
    }

    def S(bp_i: BannerPhrase): Double = {
      val si_List = campaign.bannerPhrases map (bp_j => s(bp_i, bp_j))
      si_List.sum / si_List.length
    }

    val S_List = campaign.bannerPhrases map (bp_i => (bp_i, S(bp_i)))

    val res = S_List sortWith (_._2 < _._2)

    (0 until campaign.bannerPhrases.length map {
      ind =>
        res(ind) match {
          case (bp, s) => (bp, new domain.po.Position(ind))
        }
    }).toMap
  }

  /** Permutation measure ---------------------------------------------------------------------------------*/
  def PermutationMeasure(IndPerm: List[Int]): Double = {
    /**
     * Calculate the difference between initial and new positions
     * PosPerm is position permutation related to optimal positions
     */

    val diffVector = 0 until IndPerm.length zip IndPerm map {
      case (initInd, newInd) => math.abs(initInd - newInd)
    }
    (0 /: diffVector)(_ + _) / 2

    /** We calculate the sum of position deviations */
  }

  /**
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   */
  /** createCurve --------------------------------------------------------------------------------------*/
  def createCurve(c: Campaign, t: DateTime, NumOfTimestamps: Int = 48): Curve = {
    /**
     * //weights in time: w_t
     *
     * val perf_t = c.performanceHistory.take(NumOfTimestamps)
     * val costList_t = perf_t map (performance => cost(performance))
     * val w_t = distribution(costList_t)
     *
     * //weights for BunnerPhrases: w_bp
     * val perf_bp = c.bannerPhrases map (_.performanceHistory.head)
     * val costList_bp = perf_bp map (performance => cost(performance))
     * val w_bp = distribution(costList_bp)
     *
     * //calculate mean bids (with w_t): (mean_bids)_i = sum_t (w_t*actualBids_i^t)
     * val mean_bids = c.bannerPhrases map {
     * bp => (bp.actualBidHistory.take(NumOfTimestamps), w_t).zipped.map(_.elem * _).sum
     * }
     *
     * //run the algorithm (with w_bp) to estimates parameters
     */
    new domain.po.Curve(0, 1, 1, 1, 1, t, None)
  }

  def cost(performance: Performance): Double = {
    performance.cost_context + performance.cost_search
  }

  def distribution(ListOfElements: List[Double]): List[Double] = {
    ListOfElements map (_ / ListOfElements.sum)
  }

  /**
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   */
  /** getScale-------------------------------------------------------------------------------------------*/
  def getScale(c: Campaign, Scale: Double): Double = {

    val current_cost = cost(c.performanceHistory.head)

    val t0 = c.performanceHistory.lastOption.tail.head.dateTime
    val t1 = c.performanceHistory.head.dateTime

    val delta_B = (getToleranceBudget(t0) - getRealBudget(t0)) * (T - t1.getMinuteOfDay()) / (T - t0.getMinuteOfDay()) -
      (getToleranceBudget(t1) - getRealBudget(t1))

    Scale * (1 + delta_B / current_cost)
  }

  /** Real Budget Function */
  def getRealBudget(dateTime: DateTime): Double = {
    //TODO
    //How do we define the RealBudget? by BudgetHistory?
    //val cost = c.performanceHistory.tail.head.cost_context + c.performanceHistory.tail.head.cost_search
    1
  }

  /** Tolerance Budget Function */
  def getToleranceBudget(dateTime: DateTime): Double = {
    //We need to define budget per day and Traffic function
    //TODO 
    val BudgetPerDay = 1

    getTraffic(dateTime) * BudgetPerDay / getTraffic(T)
  }

  /** Traffic Function */
  def getTraffic(dateTime: DateTime): Double = {
    //TODO
    //some function
    val TrafficPerDay = 1000

    dateTime.getMinuteOfDay() * TrafficPerDay / T
  }
  def getTraffic(MinuteOfDay: Int): Double = {
    //TODO
    //some function
    val TrafficPerDay = 1000

    MinuteOfDay * TrafficPerDay / T
  }
}