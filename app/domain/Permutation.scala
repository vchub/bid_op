package domain

import scala.reflect._
import org.joda.time._

import common._


@BeanInfo
case class Permutation(
  val id: Long = 0,
  val position: Some[Int],
  val bannerPhrase: Option[BannerPhrase] = None
)
{
  //TODO
  def bid(curve: Curve) = ???
}
