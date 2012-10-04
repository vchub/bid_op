package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = TODO
  /*
  Action {
    Ok(views.html.index("it's a service ..."))
  }
  */

  def sample(id: String, c: String) = Action {
    Ok("you tried headers: " + id + " camp: " + c).withHeaders(
      CONTENT_TYPE -> "text/html",
      CACHE_CONTROL -> "max-age=3600", 
      ETAG -> "xx"
    )
  }


}
