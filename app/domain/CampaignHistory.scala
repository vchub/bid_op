package domain

import scala.reflect._
import org.joda.time._

/** Main strucure for Optimizer
* It contains Campaign (basically Domain model) which aggregations/Histories are
* in complience with startDate and endDate
* CampaignHistory generally is created by DAO and passed to Optimizer by Controller
*/

@BeanInfo
case class CampaignHistory(
  val campaign: Campaign,
  val startDate: DateTime,
  val endDate: DateTime
){}


