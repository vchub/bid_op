package controllers

import play.api._
import play.api.mvc._
import views.html.index

import play.api.data._
import play.api.data.Forms._
import domain._
import dao.squerylorm._

case class fullForm(
  username: domain.User,
  network: domain.Network,
  campaign: domain.Campaign)

object Application extends Controller {

  val fForm: Form[fullForm] = Form(

    // Define a mapping that will handle fullForm values
    mapping(
      "username" -> nonEmptyText,
      "network" -> nonEmptyText,
      "campaign_id" -> optional(number(min = 0))) // The mapping signature matches the fullForm case class signature,
      // so we can use default apply/unapply functions here
      {
        (username, network, campaign_id) =>
          fullForm(
            User.select(username).get,
            Network.select(network).get,
            Campaign.select(username, network).filter(_.id == campaign_id.get).head)
      } {
        fullForm =>
          Some(fullForm.username.name,
            fullForm.network.name,
            Some(fullForm.campaign.id.toInt))
      })

  /**
   * Display an empty form.
   */

  def index = Action {
    Ok(views.html.index(fForm))
  }

  def sample(id: String, c: String) = Action {
    Ok("you tried headers: " + id + " camp: " + c).withHeaders(
      CONTENT_TYPE -> "text/html",
      CACHE_CONTROL -> "max-age=3600",
      ETAG -> "xx")
  }

  import dao.squerylorm.SquerylDao
  def clear_db = Action {
    val dao = new SquerylDao
    dao.clearDB
    Created("SUCCESS")
  }

  //  /**
  //   * Handle form submission.
  //   */
  //  def submit = Action { implicit request =>
  //    signupForm.bindFromRequest.fold(
  //      // Form has errors, redisplay it
  //      errors => BadRequest(html.signup.form(errors)),
  //
  //      // We got a valid User value, display the summary
  //      user => Ok(html.signup.summary(user)))
  //  }

}
