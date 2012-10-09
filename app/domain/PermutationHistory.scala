package domain

import scala.reflect._
import org.joda.time._


@BeanInfo
class PermutationHistory (
  val history: Seq[(DateTime, Seq[Permutation])] = Seq(),
  val permMap: Map[Permutation, BannerPhrase] = Map()
) {}

