package json_api

import serializers._
import serializers.yandex._

import play.api.libs.json._

object Convert {

  val typeList = Map(
    "User" -> "serializers.User",

    "Campaign" -> "serializers.Campaign",
    "List[Campaign]" -> "scala.collection.immutable.List[serializers.Campaign]",

    "Performance" -> "serializers.Performance",

    "BannerInfo" -> "serializers.yandex.BannerInfo",
    "List[BannerInfo]" -> "scala.collection.immutable.List[serializers.yandex.BannerInfo]",

    "PhrasePriceInfo" -> "serializers.PhrasePriceInfo",
    "List[PhrasePriceInfo]" -> "scala.collection.immutable.List[serializers.PhrasePriceInfo]",

    "GetBannersStatResponse" -> "serializers.GetBannersStatResponse")

  def fromJson[T](data: JsValue)(implicit mf: Manifest[T]): Option[T] = {
    import Reads._

    typeList.filter(_._2.equals(mf.toString)).headOption map {
      _._1 match {

        case "User" =>
          Json.fromJson[User](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)

        case "Campaign" =>
          Json.fromJson[Campaign](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)
        case "List[Campaign]" =>
          Json.fromJson[List[Campaign]](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)

        case "Performance" =>
          Json.fromJson[Performance](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)

        case "BannerInfo" =>
          Json.fromJson[BannerInfo](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)
        case "List[BannerInfo]" =>
          Json.fromJson[List[BannerInfo]](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)

        case "GetBannersStatResponse" => //BannerPhrases Performance during the day (alternative to XMLreport)
          Json.fromJson[GetBannersStatResponse](data) map (s => Some(s.asInstanceOf[T])) recoverTotal (e => None)

      }
    } getOrElse (None)
  }

  def toJson[T](data: T)(implicit mf: Manifest[T]): JsValue = {
    import Writes._
    typeList.filter(_._2.equals(mf.toString)).headOption map {
      _._1 match {

        case "User" =>
          Json.toJson[User](data.asInstanceOf[User])

        case "Campaign" =>
          Json.toJson[Campaign](data.asInstanceOf[Campaign])
        case "List[Campaign]" =>
          Json.toJson[List[Campaign]](data.asInstanceOf[List[Campaign]])

        case "Performance" =>
          Json.toJson[Performance](data.asInstanceOf[Performance])

        case "PhrasePriceInfo" =>
          Json.toJson[PhrasePriceInfo](data.asInstanceOf[PhrasePriceInfo])
        case "List[PhrasePriceInfo]" =>
          Json.toJson[List[PhrasePriceInfo]](data.asInstanceOf[List[PhrasePriceInfo]])
      }
    } getOrElse (JsNull)
  }
}