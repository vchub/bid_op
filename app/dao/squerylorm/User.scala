package dao.squerylorm

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import scala.reflect._
import common._


@BeanInfo
case class User(
  val name: String = ""
) extends domain.User with KeyedEntity[Long]
{
  val id: Long = 0

  @transient
  lazy val networksRel = AppSchema.campaigns.left(this)

  /**
  * default put - save to db
  */
  def put(): User = inTransaction { AppSchema.users insert this }

  /**
  * get all User's Networks
  */
  def select_networks(): List[Network] = inTransaction {
    from(AppSchema.networks)(n =>
      where( n.id in
        from(AppSchema.campaigns, AppSchema.networks) ((c, net) =>
          where( c.user_id === id and c.network_id === net.id) select(net.id)
        )
      )
      select(n)
    ).toList
  }


}
object User {
  def select(name: String): Option[User] = inTransaction{ AppSchema.users.where(
    a => a.name === name).headOption}//.single }
}


