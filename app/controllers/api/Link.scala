package controllers.api

import anorm.{NotAssigned,Pk}
 // import com.codahale.jerkson.Json._
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
  def add(callback: Option[String]) = Action { implicit request =>
    addLinkForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson),
      value => {
        LinkModel.create(value).map({ link =>
          val json = Json.toJson(link)
          // Inference goes nuts here unless we type this result
          val res: Result = callback.map({ cb => Ok(Jsonp(cb, json)) }).getOrElse(Ok(json))
          res
        }).getOrElse(BadRequest(Json.toJson(Map("error" -> Messages("link.add.failure")))))
      }
    )
  }

  /**
   * Get categories
   */
  def categories(query: Option[String], callback: Option[String]) = Action { implicit request =>
    val json = Json.toJson(LinkModel.getAllCategories)
    callback.map({ cb =>
      Ok(Jsonp(cb, json))
    }).getOrElse(Ok(json))
  }

  /**
   * Remove a link.
   */
  def delete(id: Long, callback: Option[String]) = Action { implicit request =>

    val link = LinkModel.delete(id)
    val json = Json.toJson(link)
    callback.map({ cb =>
      Ok(Jsonp(cb, json))
    }).getOrElse(Ok(json))
  }

  /**
   * Get all links.
   */
  def list(userId: Long, callback: Option[String]) = Action { implicit request =>
    val links = LinkModel.getAllForUser(userId)

    val json = Json.toJson(links)
    callback match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }

  /**
   * Mark a link as read for a user.
   */
  def read(linkId: Long, userId: Long, callback: Option[String]) = Action { implicit request =>
    val ul = LinkModel.read(linkId, userId)

    val json = Json.toJson(ul)
    callback match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }

  /**
   * Unmark a link as read for a user.
   */
  def unRead(linkId: Long, userId: Long, callback: Option[String]) = Action { implicit request =>
    val ul = LinkModel.unread(linkId, userId)
    println("HASDAJSDASD");
    val json = Json.toJson(ul)
    callback match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }
}