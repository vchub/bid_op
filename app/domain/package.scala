package object domain {

  import org.joda.time._

  type ActualBidHistory = Seq[TSValue[Double]]
  type RecommendationHistory = Seq[TSValue[Double]]
  type NetAdvisedBidsHistory = Seq[NetAdvisedBids]
  type PermutationHistory = List[Permutation]
  type PerformanceHistory = Seq[Performance]
  type BudgetHistory =  Seq[TSValue[Double]]
  type EndDateHistory =  Seq[TSValue[DateTime]]
  type CurveHistory =  Seq[Curve]



}
