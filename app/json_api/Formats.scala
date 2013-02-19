package json_api

import play.api.libs.json._
import play.api.libs.functional.syntax._

import serializers._
import serializers.yandex._

/************* JSON WRITEs and READs for creating REQUESTs and handling RESULTs ************/
/************* or just define JSON FORMATs 									    ************/

object Reads { /* --------------- fromJson -------------- */
  import play.api.libs.json.Reads._

  implicit lazy val user = Json.reads[User]
  implicit lazy val campaign = Json.reads[Campaign]
  implicit lazy val performance = Json.reads[Performance]

  implicit lazy val bannerPhraseInfo = Json.reads[BannerPhraseInfo]
  implicit lazy val bannerInfo = Json.reads[BannerInfo]
}

object Writes { /* --------------- toJson -------------- */
  import play.api.libs.json.Writes._

  implicit lazy val user = Json.writes[User]
  implicit lazy val campaign = Json.writes[Campaign]
  implicit lazy val performance = Json.writes[Performance]
  
  implicit lazy val phrasePriceInfo = Json.writes[PhrasePriceInfo]
}