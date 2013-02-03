package controllers

import models.LinkModel
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import util.JsonFormats._

object Application extends Controller {

  def index(category: Option[String]) = Action {

    val cats = Json.toJson(LinkModel.getAllCategories)
    val links = Json.toJson(LinkModel.getAllForUser(1, category))

    Ok(views.html.index(category, cats.toString, links.toString, controllers.api.Link.addLinkForm))
  }

}