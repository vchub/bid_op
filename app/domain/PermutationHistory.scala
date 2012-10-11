package domain

import scala.reflect._
import org.joda.time._


/** History of Permutations associated w/ Curve
* TODO: make an example
*/
@BeanInfo
class PermutationHistory (
  val seq: Seq[Permutation]
) {}

