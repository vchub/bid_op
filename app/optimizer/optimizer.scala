package optimizer

import domain._
import org.joda.time._
import org.joda.time.DateTime

class Optimizer(_t: DateTime, _c: Campaign) {
  val c = _c
  val t = _t

  //c.performanceHistory.tail.head.dateTime
  //c.performanceHistory.head.cost_context + c.performanceHistory.head.cost_search

  //create Permutations, local and optimal respectively
  def createLocalPermutation: Permutation = new domain.po.Permutation(0, dateTime = t, permutation = createPermutationMap)
  def createOptimalPermutation: Permutation = new domain.po.Permutation(0, dateTime = t, permutation = createOptimalPermutationMap)
    
  def createPermutationMap: Map[BannerPhrase, Position] = {
    //val optPerm = c.curves.head.optimalPermutation
    //TODO
    (for (b <- c.bannerPhrases; i <- 0 to c.bannerPhrases.length)
      yield (b, domain.po.Position(i))).toMap
  }
 
  def createOptimalPermutationMap: Map[BannerPhrase, Position] = {
    //TODO
    (for (b <- c.bannerPhrases; i <- 0 to c.bannerPhrases.length)
      yield (b, domain.po.Position(i))).toMap
  }
  
  
  //create Curve
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
  def getScaleParameter(ScaleParameter: Double): Double = 1
   //val delta_B = (bF(t0) - cost(t0)) * (T - t1) / (T - t0) - (bF(t1) - cost(t1))
   //ScaleParameter(1+delta_B/cost(t1))

}