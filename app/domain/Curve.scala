package domain

import org.joda.time._


trait Curve extends OrderedByDateTime[Curve]{
  def id: Long
  def a: Double
  def b: Double
  def c: Double
  def d: Double

  def dateTime: DateTime
  def optimalPermutation: Option[Permutation]
}

