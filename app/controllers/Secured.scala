package controllers

import play.api.mvc._
import dao.squerylorm.SquerylDao
import play.api.libs.concurrent.Akka
import java.util.concurrent.TimeUnit

import play.api.Play.current

/**
 * Provide security features
 */
trait Secured extends Controller {

  /**
   * Action for checking on authenticated users (with right name and password)
   */
  def IsAuth(user_name: String, f: (SquerylDao, domain.User) => Request[AnyContent] => Result) = {
    Action { request =>
      Async { // use Async result to provide concurrent usage of BID service
        Akka.future { // use Promise to make consuming computations
          val dao = new SquerylDao
          request.headers.get("password") match {
            case None => BadRequest("PASSWORD requestHeader is EMPTY...")
            case Some(password) =>
              dao.getUser(user_name, password).headOption match {
                case None => NotFound("User NOT FOUND... Invalid name or password...")
                case Some(user) => f(dao, user)(request)
              }
          }
          // if service handles request too slow => return Timeout response  
        }.orTimeout("Oops, TIMEOUT while calling BID server...", 10, TimeUnit.SECONDS).map(
          eitherResultOrTimeout => eitherResultOrTimeout.fold(
            result => result,
            timeout => RequestTimeout(timeout)))
      }
    }
  }

}