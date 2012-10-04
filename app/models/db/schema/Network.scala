package models.db
package schema

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import scala.reflect._
import AppSchema._



@BeanInfo
case class Network(     //Google, Yandex, etc
  val name: String = ""
  ) extends KeyedEntity[Long]
  {
    val id: Long = 0

    lazy val users = AppSchema.campaigns.right(this)

    /**
    * default put - save to db
    * @param
    * @return Campaign
    */
    def put(): Network = inTransaction { networks insert this }
  }

object Network {
  def select(name: String): List[Network] = inTransaction{ networks.where(a => a.name === name).toList} //.single }
}


