package domain

import scala.reflect._


@BeanInfo
case class Banner(
  val id: Long = 0,
  val bannerPhrases: Seq[BannerPhrase] = Seq()
) {}
