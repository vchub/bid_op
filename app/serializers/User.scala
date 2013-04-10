package serializers

import play.api.libs.json._

case class User(
  val name: String,
  val password: String) extends domain.User {
  @transient var id: Long = 0
}

object User extends Function2[String, String, User] {
  def _apply(user: domain.User): User = User(
    name = user.name,
    password = user.password)
}

