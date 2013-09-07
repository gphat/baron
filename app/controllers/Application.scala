package controllers

import models.LinkModel
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import util.JsonFormats._

object Application extends Controller {

  def index(category: Option[String]) = Action {

    Ok(views.html.main(""))
  }

}