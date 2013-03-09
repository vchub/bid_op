package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import domain._
import dao.squerylorm._

import play.api.libs.{ Comet }
import play.api.libs.iteratee._

object Application extends Controller with Secured {

  /**
   * A String Enumerator producing a formatted Time message every 100 millis.
   * A callback enumerator is pure an can be applied on several Iteratee.
   */
  lazy val clock: Enumerator[String] = {

    import play.api.libs.concurrent._
    import scala.concurrent.duration._
    import play.api.libs.concurrent.Execution.Implicits._

    import java.util._
    import java.text._

    val dateFormat = new SimpleDateFormat("HH mm ss")

    Enumerator.generateM {
      Promise.timeout(Some(dateFormat.format(new Date)), 100 milliseconds)
    }
  }

  def index = Action {
    Ok(views.html.index())
  }

  def liveClock = Action {
    Ok.stream(clock &> Comet(callback = "parent.clockChanged"))
  }

  def sample(id: String, c: String) = Action {
    Ok("you tried headers: " + id + " camp: " + c).withHeaders(
      CONTENT_TYPE -> "text/html",
      CACHE_CONTROL -> "max-age=3600",
      ETAG -> "xx")
  }

  /**
   * clear database function
   * is used with GET request from web client
   */
  def clearDB(user_name: String) = IsAuth(
    user_name,
    (dao, user) => implicit request => if (dao.clearDB) Ok else BadRequest)

}


