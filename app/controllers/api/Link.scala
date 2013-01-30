package controllers.api

import anorm.{NotAssigned,Pk}
import com.codahale.jerkson.Json._
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
      "org"                 -> longNumber,
      "position"            -> number,
      "url"                 -> text,
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


    // request.body.asJson match {
    //   case Some(j) => {
    //     val link: Link = Json.fromJson[Link](j)
    //     val resp = Json.toJson(LinkModel.create(link))
    //     callback match {
    //       case Some(cb) => Ok(Jsonp(cb, resp))
    //       case _ => Ok(resp)
    //     }
    //   }
    //   case None => BadRequest("Expecing JSON Data")
    // }
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
  def list(callback: Option[String]) = Action { implicit request =>
    val links = LinkModel.getAll

    val json = Json.toJson(links)
    callback match {
      case Some(callback) => Ok(Jsonp(callback, json))
      case None => Ok(json)
    }
  }
}