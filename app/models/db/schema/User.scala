package models.db
package schema

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

import scala.reflect._

import AppSchema._
import common._


@BeanInfo
case class User(
  val name: String = ""
  ) extends KeyedEntity[Long]
  {
    @transient
    val id: Long = 0

    @transient
    lazy val networks = AppSchema.campaigns.left(this)

    /**
    * default put - save to db
    * @param
    * @return User
    */
    def put(): User = inTransaction { users insert this }

    /**
    * get all User's Networks
    * @param
    * @return List[Network]
    */
    def select_networks(): List[Network] = inTransaction {
      from(AppSchema.networks)(n =>
        where( n.id in
          from(campaigns, networks) ((c, net) =>
            where( c.user_id === id and c.network_id === net.id) select(net.id)
          )
        )
        select(n)
      ).toList
    }


  }
object User {
  def select(name: String): List[User] = inTransaction{ users.where(a => a.name === name).toList}//.single }
}


