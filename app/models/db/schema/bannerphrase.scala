package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._

import scala.reflect._

import AppSchema._
import common._


@BeanInfo
case class BannerPhrase(
  val id: Long = 0,
  val banner_id: Long = 0,//fk
  val phrase_id: Long = 0,//fk
  val region_id: Long = 0,  //fk
  val bid: Double = 0       // bid or price for cpc or rtb
)extends KeyedEntity[Long]
{
}
