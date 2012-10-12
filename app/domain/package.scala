package object domain {

  import org.joda.time._

  //type TimeSeries
  type TimeSeries[+T] = List[(DateTime, T)]
  type PerformanceHistory = List[Performance]
  type PermutationHistory = List[Permutation]
  type CurveHistory = List[Curve]
  type NetAdvisedBidsHistory = List[NetAdvisedBids]
  type RecommendationHistory = TimeSeries[Double]
  type ActualBidHistory = TimeSeries[Double]
  type BudgetHistory = TimeSeries[Double]


  case class NetAdvisedBids(val a:Double, val b:Double, val c:Double, val d:Double){}
}
