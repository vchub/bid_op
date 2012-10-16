package object domain {

  import org.joda.time._

  //TimeSeries Value
  case class TSValue[T](val date: DateTime, val elem: T) extends Ordered[TSValue[T]]{
    def compare(that: TSValue[T]) = this.date compareTo that.date
  }

  case class NetAdvisedBids(val a:Double, val b:Double, val c:Double, val d:Double){}


  /*
  // histories
  type TimeSeries[T] = List[TSValue[T]]

  type PerformanceHistory = List[Performance]
  type PermutationHistory = List[Permutation]
  type CurveHistory = List[Curve]
  type NetAdvisedBidsHistory = TimeSeries[NetAdvisedBids]
  type RecommendationHistory = TimeSeries[Double]
  type ActualBidHistory = TimeSeries[Double]
  type BudgetHistory =  List[TSValue[Double]]
  */


}
