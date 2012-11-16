package controllers

import play.api._
import play.api.mvc._

import com.codahale.jerkson.Json

import domain.{User, Campaign, Network}
import dao.squerylorm.SquerylDao



object UserController extends Controller {

  /**
  * TODO: get user's Networks
  * now it's user's name and id only
  * route: GET /user/:id
  */
  def user(user_name: String) = Action {
    val dao = new SquerylDao
    dao.getUser(user_name).headOption match {
      case None => NotFound
      case Some(user) => Ok(Json generate user).as(JSON)
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

