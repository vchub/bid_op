package json_api

import play.api.libs.json._
import play.api.libs.functional.syntax._

import serializers._
import serializers.yandex._

/************* JSON WRITEs and READs for creating REQUESTs and handling RESULTs ************/
/************* or just define JSON FORMATs 									    ************/

object Formats {

  /* serializers */
  implicit lazy val user = Format(Reads.user, Writes.user)
  implicit lazy val campaign = Format(Reads.campaign, Writes.campaign)

  implicit lazy val phrasePriceInfo = Format(Reads.phrasePriceInfo, Writes.phrasePriceInfo)
  implicit lazy val performance = Format(Reads.performance, Writes.performance)
  //implicit lazy val campaignList = Format(Reads.campaignList, Writes.campaignList)

  /* serializers.yandex */
  implicit lazy val bannerPhraseInfo = Format(Reads.bannerPhraseInfo, Writes.bannerPhraseInfo)
  implicit lazy val bannerInfo = Format(Reads.bannerInfo, Writes.bannerInfo)
  implicit lazy val bannerReport = Format(Reads.bannerReport, Writes.bannerReport)
}

object Reads {
  import play.api.libs.json.Reads._

  /* serializers */
  implicit lazy val user = Json.reads[User]
  implicit lazy val campaign = Json.reads[Campaign]

  implicit lazy val phrasePriceInfo = Json.reads[PhrasePriceInfo]
  implicit lazy val performance = Json.reads[Performance]
  //implicit lazy val campaignList = (__ \ "key1").read(list[Campaign])

  /* serializers.yandex */
  implicit lazy val bannerPhraseInfo = Json.reads[BannerPhraseInfo]
  implicit lazy val bannerInfo = Json.reads[BannerInfo]
  implicit lazy val bannerReport =
    (__ \ 'data).read[List[BannerInfo]].map { l => BannerReport(l) } // covariant map

  //((__ \ "data").lazyRead(list[BannerInfo](bannerInfo)))(BannerReport.apply _)
  /*and
    (__ \ "trash").read[String]
    )(BannerReport)*/

  //val areads = (__ \ 'value).read[List[Int]].map{ l => A(l) } // covariant map
  //val awrites = (__ \ 'value).write[List[Int]].contramap{ a: A => a.value } // contravariant map
  //val aformat = (__ \ 'value).format[List[Int]].inmap(  (l: List[Int]) => A(l), (a: A) => a.value ) // invariant = both covariant and contravariant
}

object Writes {
  import play.api.libs.json.Writes._

  /* serializers */
  implicit lazy val user = Json.writes[User]
  implicit lazy val campaign = Json.writes[Campaign]

  implicit lazy val phrasePriceInfo = Json.writes[PhrasePriceInfo]
  implicit lazy val performance = Json.writes[Performance]
  //implicit lazy val campaignList = (__ \ "key1").write(list[Campaign])

  /* serializers.yandex */
  implicit lazy val bannerPhraseInfo = Json.writes[BannerPhraseInfo]
  implicit lazy val bannerInfo = Json.writes[BannerInfo]
  implicit lazy val bannerReport =
    (__ \ 'data).write[List[BannerInfo]].contramap { a: BannerReport => a.data } // contravariant map

  /*((__ \ "data").lazyWrite(list[BannerInfo]) and
    (__ \ "trash").write[String]
  )(unlift(BannerReport.unapply))*/
}



/*
 * val areads = (__ \ 'value).read[List[Int]].map{ l => A(l) } // covariant map
  val awrites = (__ \ 'value).write[List[Int]].contramap{ a: A => a.value } // contravariant map
  val aformat = (__ \ 'value).format[List[Int]].inmap(  (l: List[Int]) => A(l), (a: A) => a.value ) // invariant = both covariant and contravariant
 */