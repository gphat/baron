package controllers.api

import emp.JsonFormats._
import com.codahale.jerkson.Json._
import controllers._
import models._
import play.api._
import play.api.mvc._
import play.api.libs.Jsonp
import play.api.libs.json.Json

object Link extends Controller {

  /**
   * Add a link.
   */
  def add(callback: Option[String]) = Action { implicit request =>

    request.body.asJson match {
      case Some(j) => {
        val link: Link = Json.fromJson[Link](j)
        val resp = Json.toJson(LinkModel.create(link))
        callback match {
          case Some(cb) => Ok(Jsonp(cb, resp))
          case _ => Ok(resp)
        }
      }
      case None => BadRequest("Expecing JSON Data")
    }
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

}