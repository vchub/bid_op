package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json._

import domain.{ User, Campaign, Network }
import dao.squerylorm.SquerylDao

object UserController extends Controller with Secured {

  /**
   * TODO: get user's Networks
   * now it's user's name and id only
   * route: GET /user/:id
   */
  def user_old(user_name: String) = Action {
    val dao = new SquerylDao
    dao.getUser(user_name).headOption match {
      case None => NotFound
      case Some(user) => Ok(Json.toJson(serializers.User._apply(user))(common.Formats.user)).as(JSON)
    }
  }

  def user(user_name: String) = IsAuth(user_name, (dao, user) => request => Ok(Json.toJson(serializers.User._apply(user))(common.Formats.user)) as (JSON))

  def createUser = Action { implicit request =>
    request.body.asJson match {
      case None => BadRequest("Invalid json body")
      case Some(jbody) =>
        try {
          val sUser = serializers.User._apply(jbody)
          val dao = new SquerylDao
          val newUser = dao.create(sUser)
          // respond with CREATED header and User body
          Created(Json.toJson(sUser)(common.Formats.user)) as (JSON)
        } catch {
          case e =>
            println(e) //TODO: change to log
            BadRequest("exception caught: " + e)
        }

    }
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

