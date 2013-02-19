package json_api

import serializers._
import play.api.libs.json._

object Convert {

  val typeList = Map(
    "User" -> "serializers.User")

  def fromJson[T](data: JsValue)(implicit mf: Manifest[T]): Option[T] = {
    import Reads._

    typeList.filter(_._2.equals(mf.toString)).headOption map {
      _._1 match {

        case "User" =>
          Json.fromJson[User](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)

      }
    } getOrElse (None)
  }

  def toJson[T](data: T)(implicit mf: Manifest[T]): JsValue = {
    import Writes._
    typeList.filter(_._2.equals(mf.toString)).headOption map {
      _._1 match {

        case "User" =>
          Json.toJson[User](data.asInstanceOf[User])
      }
    } getOrElse (JsNull)
  }
}