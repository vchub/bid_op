package common

import java.sql.Timestamp
import java.util.Date
import java.text.SimpleDateFormat
import org.joda.time._


object Helpers {
  val app_formatter = format.DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  /**
  * get org.joda.time.DateTime from String of format ("yyyy-MM-dd hh:mm:ss")
  * @return Date
  */
  def get_DateTime_from_standart_string(str: String): DateTime = app_formatter.parseDateTime(str)
  def get_from_standart_string_DateTime(date: DateTime): String = date.toString(app_formatter)

  /**
  * get java.util.Date from Sting of format ("yyyy-MM-dd hh:mm:ss")
  * @return Date
  */
  def get_Date_from_standart_string(str: String): Date = {
    (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).parse(str)
  }
}

