package controllers

import play.api.mvc._
import dao.squerylorm.SquerylDao

//import scala.concurrent._
import play.api.libs.concurrent.Akka
import java.util.concurrent.TimeUnit

//import play.api.Play.current
import scala.concurrent.{ Future, Promise }
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Provide security features
 */
trait Secured extends Controller {

  /**
   * Action for checking on authenticated users (with right name and password)
   */
  def IsAuth(user_name: String, f: (SquerylDao, domain.User) => Request[AnyContent] => Result) = {
    Action { request =>
      val futureResult = Future[Result] {
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
      // if service handles request too slow => return Timeout response
      val timeoutFuture = play.api.libs.concurrent.Promise.timeout(
        message = "Oops, TIMEOUT while calling BID server...",
        duration = 10,
        unit = TimeUnit.SECONDS)

      Async {
        Future.firstCompletedOf(Seq(futureResult, timeoutFuture)).map {
          case f: Result => f
          case t: String => InternalServerError(t)
        }
      }
    }
  }

}

/*
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
 * 
 */