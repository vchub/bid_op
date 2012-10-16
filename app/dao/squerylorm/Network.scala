package dao.squerylorm

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import scala.reflect._



@BeanInfo
case class Network(     //Google, Yandex, etc
  val name: String = ""
  ) extends KeyedEntity[Long]
  {
    val id: Long = 0

    lazy val users = AppSchema.campaigns.right(this)

    /**
    * default put - save to db
    */
    def put(): Network = inTransaction { AppSchema.networks insert this }
  }

object Network {
  def select(name: String): List[Network] = inTransaction{ AppSchema.networks.where(a => a.name === name).toList} //.single }
}


