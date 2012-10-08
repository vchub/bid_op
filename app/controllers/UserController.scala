package controllers

import play.api._
import play.api.mvc._

import com.codahale.jerkson.Json

import domain._



object UserController extends Controller {

  /**
  * get user's Networks
  **/
  // GET /user/:id
  def user(user_name: String) = Action {
    val user = domain.User.select(user_name)
    (user) match {
      case(Nil) => NotFound
      case _ => {
        Ok(Json generate user).as(JSON)
      }
    }
  }


  def sample(id: String, c: String) = Action {
    Ok("you tried headers: " + id + " camp: " + c).withHeaders(
      CONTENT_TYPE -> "text/html",
      CACHE_CONTROL -> "max-age=3600", 
      ETAG -> "xx"
    )
  }


}

