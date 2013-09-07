package controllers.api

import anorm.{NotAssigned,Pk}
import controllers._
import models._
import org.joda.time.DateTime
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import play.api.libs.Jsonp
import play.api.libs.json.Json
import util.JsonFormats._

object Link extends Controller {

  def addLinkForm = Form(
    mapping(
      "id"                  -> ignored(NotAssigned:Pk[Long]),
      "url"                 -> nonEmptyText,
      "poster"              -> longNumber,
      "category"            -> text,
      "position"            -> longNumber,
      "description"         -> text,
      "date_created"        -> ignored(new DateTime())
    )(models.Link.apply)(models.Link.unapply)
  )

  /**
   * Add a link.
   */
  def add = Action { implicit request =>
    addLinkForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson),
      value => {
        LinkModel.create(value).map({ link =>
          Ok(Json.toJson(link))
        }).getOrElse(BadRequest(Json.toJson(Map("error" -> Messages("link.add.failure")))))
      }
    )
  }

  /**
   * Get categories
   */
  def categories(query: Option[String]) = Action { implicit request =>
    Ok(Json.toJson(LinkModel.getAllCategories))
  }

  /**
   * Remove a link.
   */
  def delete(id: Long) = Action { implicit request =>

    val link = LinkModel.delete(id)
    Ok(Json.obj("status" -> "OK"))
  }

  /**
   * Get a specific link
   */
  def item(id: Long) = Action { implicit request =>
    LinkModel.getById(id).map({ link =>
      val json = Json.toJson(link)
      Ok(json)
    }).getOrElse(NotFound(Json.obj("status" -> "KO", "message" -> "api.unknown.entity")))
  }

  /**
   * Get all links.
   */
  def list(category: Option[String]) = Action { implicit request =>
    Ok(Json.toJson(LinkModel.getAll))
  }

  /**
   * Mark a link as read for a user.
   */
  def read(linkId: Long, userId: Long) = Action { implicit request =>
    val ul = LinkModel.read(linkId, userId)

    Ok(Json.toJson(ul))
  }

  /**
   * Unmark a link as read for a user.
   */
  def unRead(linkId: Long, userId: Long) = Action { implicit request =>
    val ul = LinkModel.unread(linkId, userId)
    Ok(Json.toJson(ul))
  }
}