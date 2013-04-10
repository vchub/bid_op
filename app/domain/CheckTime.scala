package domain

import org.joda.time._


trait CheckTime extends OrderedByDateTime[CheckTime]{
  def id: Long
  def elem: Double
  def dateTime: DateTime
}

