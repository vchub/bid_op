package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json._

import domain.{ User, Campaign, Network }
import dao.squerylorm.SquerylDao

import json_api.Convert._

object UserController extends Controller with Secured {

  /**
   * TODO: get user's Networks
   * now it's user's name and id only
   * route: GET /user/:id
   */
  def user_old(user_name: String) = Action {
    val dao = new SquerylDao
    dao.getUser(user_name) match {
      case None => NotFound
      case Some(user) => Ok(toJson[serializers.User](serializers.User._apply(user))).as(JSON)
    }
  }

  def user(user_name: String) = IsAuth(user_name, (dao, user) => request => Ok(toJson[serializers.User](serializers.User._apply(user))) as (JSON))

  def createUser = Action(parse.json) { implicit request =>
    fromJson[serializers.User](request.body) map { sUser =>
      val dao = new SquerylDao
      if (!dao.getUser(sUser.name).isDefined) { //if this User is not exists
        val newUser = dao.create(sUser)
        // respond with CREATED header and User body
        Created(toJson[serializers.User](sUser)) as (JSON)
      } else BadRequest
    } getOrElse BadRequest
  }

  def sample(id: String, c: String) = Action {
    Ok("you tried headers: " + id + " camp: " + c).withHeaders(
      CONTENT_TYPE -> "text/html",
      CACHE_CONTROL -> "max-age=3600",
      ETAG -> "xx")
  }
}


/*
 def user(user_name: String) = Action { implicit request =>
    request.body.asJson match {
      case None => BadRequest("Invalid json body")
      case Some(jbody) =>
        try {
          val pass = Json.parse[Map[String, String]](jbody.toString)
          val password = pass.get("password").get

          val dao = new SquerylDao
          dao.getUser(user_name, password).headOption match {
            case None => NotFound
            case Some(user) => Ok(Json generate user).as(JSON)
          }
        } catch {
          case e =>
            println(e) //TODO: change to log
            BadRequest("exception caught: " + e)
        }

    }
  }
 */

