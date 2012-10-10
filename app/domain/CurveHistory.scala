package domain

import scala.reflect._


@BeanInfo
class CurveHistory(
  val seq: Seq[Curve] = Seq()
) {}

