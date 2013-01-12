package serializers

import com.codahale.jerkson.Json

case class User(
  val name: String,
  val password: String) extends domain.User {
  @transient var id: Long = 0
}
 
object User {
  def apply(user: domain.User): User = User(
    name = user.name,
    password = user.password)

  def apply(jsonstring: String): User = Json.parse[User](jsonstring)
}

