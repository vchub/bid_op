package optimizer

import domain._
import org.joda.time._
import breeze.linalg._
import breeze.numerics._

class Optimizer {
  val viz = new Visualization

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
    def perm(ipPerm: List[(domain.BannerPhrase, domain.Position, Int)]): List[(domain.BannerPhrase, domain.Position, Int)] =
      if (ipPerm.length <= n) scala.util.Random.shuffle(ipPerm)
      else scala.util.Random.shuffle(ipPerm.take(n)) ::: perm(ipPerm.drop(n))

    //get optimal permutation from the current curve    
    val optPerm = campaign.curves.head.optimalPermutation.get.permutation.toList

    //create local position permutation
    val locPerm = perm(optPerm.zipWithIndex map { case ((bp, pos), ind) => (bp, pos, ind) })

    val locPermMap = (locPerm.zipWithIndex map {
      case ((bp, pos, ind), new_ind) =>
        (bp, optPerm(new_ind)._2)
    }).toMap

    val iPerm = locPerm map { case (bp, pos, ind) => ind }

    (locPermMap, iPerm)
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
            if ((perm_List(k).permutation.get(bp_i).get.position <
              perm_List(k).permutation.get(bp_j).get.position))
              cost_List(k)
            else if ((perm_List(k).permutation.get(bp_i).get.position ==
              perm_List(k).permutation.get(bp_j).get.position))
              0
            else
              -cost_List(k)
          }
      }).toList
      sij_List.sum
    }

    def S(bp_i: BannerPhrase): Double = {
      val si_List = campaign.bannerPhrases map (bp_j => s(bp_i, bp_j))

      /*viz.getPlot(
        linspace(1.0, si_List.length.toDouble, si_List.length).toArray,
        si_List.toArray, bp_i.id.toString)
*/
      si_List.sum / si_List.length

    }

    val S_List = campaign.bannerPhrases map (bp_i => (bp_i, S(bp_i)))

    val res = S_List sortWith (_._2 > _._2)

    /*viz.getPlot(
      linspace(1.0, res.length.toDouble, res.length).toArray,
      res.map(_._2).toArray)*/

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
   */
  /** createCurve --------------------------------------------------------------------------------------*/
  def createCurve(c: Campaign, t: DateTime, NumOfTimestamps: Int = 48): Curve =
    if (false) new domain.po.Curve(0, 1, 1, 1, 1, t, None)
    else {

      //weights in time: w_t
      val perf_t = c.performanceHistory.take(NumOfTimestamps)
      val costList_t = perf_t map (performance => cost(performance))
      val w_t = distribution(costList_t)

      //weights for BunnerPhrases: w_bp
      val perf_bp = c.bannerPhrases map (_.performanceHistory.head)
      val costList_bp = perf_bp map (performance => cost(performance))
      val w_bp = distribution(costList_bp)

      //calculate mean bids (with w_t): (mean_bids)_i = sum_t (w_t*actualBids_i^t)
      /*val mean_bids = (c.bannerPhrases map {
        bp => (bp, (bp.actualBidHistory.take(NumOfTimestamps), w_t).zipped.map(_.elem * _).sum)
      }).toMap
       */

      //Relative bids with old optimal permutation
      val perm_old = c.curves.head.optimalPermutation.get

      val OldPos_RelativeBids = c.bannerPhrases.map(bp =>
        (perm_old.permutation.get(bp).get.position.toDouble, perm_old.permutation.get(bp).get.bid(c.curves.head)))

      /*viz.getPlot(
        OldPos_RelativeBids.map(_._1).toArray,
        OldPos_RelativeBids.map(_._2).toArray,
        "a=" + c.curves.head.a.toString + "\n b=" + c.curves.head.b.toString,
        fName = "OldPos_RelBids")*/

      //Mean relative bids with new optimal permutation
      val perm = createOptimalPermutation(c, t, NumOfTimestamps)
      /*
        val mean_relative_bids = (c.bannerPhrases map {
        bp =>
        (bp,
        ((c.permutationHistory.take(NumOfTimestamps) map {
        p => p.permutation.get(bp).get.bid(c.curves.head)
        }), w_t).zipped.map(_ * _).sum)
        }).toMap
       */

      def func(curve: Curve, pos: Double): Double = 1 / (1 + math.exp(-curve.a * pos - curve.b))

      val mean_relative_bids = (c.bannerPhrases map {
        bp =>
          (bp, func(c.curves.head,
            ((c.permutationHistory.take(NumOfTimestamps) map {
              p => p.permutation.get(bp).get.position.toDouble
            }), w_t).zipped.map(_ * _).sum))
      }).toMap

      val NewPos_MeanRelativeBids = c.bannerPhrases.map(bp =>
        (perm_old.permutation.get(bp).get.position.toDouble, perm.permutation.get(bp).get.bid(c.curves.head) ))
      //change perm to perm_old, mean_relative_bids.get(bp).get

      /*viz.getPlot(
        NewPos_MeanRelativeBids.map(_._1).toArray,
        NewPos_MeanRelativeBids.map(_._2).toArray,
        "a=" + c.curves.head.a.toString + "\n b=" + c.curves.head.b.toString,
        fName = "NewPos_MeanRelBids")*/

      //run the algorithm (with w_bp and mean_bids) to estimates parameters
      val par = alg(
        NewPos_MeanRelativeBids.map(_._1).toArray,
        NewPos_MeanRelativeBids.map(_._2).toArray,
        c.curves.head,
        w_bp.toArray)
      //mean_relative_bids.toList.map({ case (bp, b) => b })

      val cur = new domain.po.Curve(0, par(0), par(1), 1, 1, t, None)

      /*viz.getPlot2(
        OldPos_RelativeBids.map(_._1).toArray,
        OldPos_RelativeBids.map(_._2).toArray,
        NewPos_MeanRelativeBids.map(_._1).toArray,
        NewPos_MeanRelativeBids.map(_._2).toArray,
        c.curves.head,
        cur,
        fName = "Parameter Changing 3")*/

      //new domain.po.Curve(0, 1, 1, 1, 1, t, None)
      cur
    }

  def alg(
    _x: Array[Double],
    _y: Array[Double],
    curve: domain.Curve,
    _w_bp: Array[Double],
    eps: Double = 1e-5): Array[Double] = {

    /**
     * y - target vector
     * X - data matrix
     * w_bp - observation weights
     * eps - stop value
     */
    val y = DenseVector[Double](_y)
    val X = DenseMatrix.horzcat(
      DenseMatrix.create[Double](_y.length, 1, _x),
      DenseMatrix.create[Double](_y.length, 1, linspace(1.0, 1.0, _y.length).toArray))
    val W_bp = diag(DenseVector[Double](_w_bp))

    def NG(Phi: DenseVector[Double]): DenseVector[Double] = {
      val p = f(X, Phi)
      val J = diag(p :* (DenseVector.ones[Double](y.size) :- p)) * X
      val delta_Phi = pinv(J.t * W_bp * J) * J.t * W_bp * (y :- p)

      if (sum(delta_Phi :* delta_Phi) < eps) Phi
      else NG(Phi + delta_Phi)
    }

    //*/
    def f(X: DenseMatrix[Double], Phi: DenseVector[Double]): DenseVector[Double] = {
      val expXp = exp(-X * Phi).toArray.toList
      DenseVector((expXp map (x => 1 / (1 + x))).toArray)
    }

    NG(DenseVector[Double](curve.a, curve.b)).toArray
  }

  def cost(performance: Performance): Double = {
    performance.cost_context + performance.cost_search
  }

  def distribution(ListOfElements: List[Double]): List[Double] = {
    ListOfElements map (_ / ListOfElements.sum)
  }

  /**
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