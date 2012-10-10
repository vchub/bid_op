package domain

import scala.reflect._
import org.joda.time._


/** PermutationHistory is history (DateTime, Seq[Permutation]).
* But it also contains a mapper from Permutation to BannerPhrase.
* So it's possible to get BannerPhrase.PerformanceHistory and etc.
* TODO: make an example
*/
@BeanInfo
class PermutationHistory (
  val seq: Seq[(DateTime, Map[BannerPhrase, Permutation])] = Seq(),
) {}

