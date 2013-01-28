package controllers

import play.api.mvc._
import dao.squerylorm.SquerylDao

/**
 * Provide security features
 */
trait Secured extends Controller {

  /**
   * Action for checking on authenticated users (with right name and password)
   */
  def IsAuth(user_name: String, f: (SquerylDao, domain.User) => Request[AnyContent] => Result) = {
    Action { request =>
      val dao = new SquerylDao
      request.headers.get("password") match {
        case None => BadRequest("PASSWORD requestHeader is EMPTY...")
        case Some(password) =>
          dao.getUser(user_name, password).headOption match {
            case None => NotFound("User NOT FOUND... Invalid name or password...")
            case Some(user) => f(dao, user)(request)
          }
      }
    }
  }

}