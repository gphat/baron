package util

import anorm.{Id,NotAssigned}
import models._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import play.api.i18n.Messages
import play.api.libs.json.Json._
import play.api.libs.json._

/**
 * Code for converting Emperor entities into JSON.
 */
object JsonFormats {

  val dateFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'")
  val dateFormatterUTC = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'").withZoneUTC()

  private def optionLongtoJsValue(maybeId: Option[Long]) = maybeId.map({ l => JsNumber(l) }).getOrElse(JsNull)

  private def optionI18nStringtoJsValue(maybeId: Option[String]) = maybeId.map({ s => JsString(Messages(s)) }).getOrElse(JsNull)
  private def optionStringtoJsValue(maybeId: Option[String]) = maybeId.map({ s => JsString(s) }).getOrElse(JsNull)

  /**
   * JSON conversion for Category
   */
  implicit object CategoryFormat extends Format[Category] {
    def reads(json: JsValue): JsResult[Category] = JsSuccess(Category(
      name = (json \ "name").as[String]
    ))

    def writes(obj: Category): JsValue = {
      val edoc: Map[String,JsValue] = Map(
        "name" -> JsString(obj.name)
      )
      toJson(edoc)
    }
  }

  /**
   * JSON conversion for Link
   */
  implicit object LinkFormat extends Format[Link] {
    def reads(json: JsValue): JsResult[Link] = JsSuccess(Link(
      id = Id((json \ "id").as[Long]),
      url = (json \ "url").as[String],
      poster = (json \ "poster").as[Long],
      category = (json \ "category").as[String],
      position = (json \ "position").as[Int],
      description = (json \ "description").as[String],
      dateCreated = (json \ "dateCreated").as[Option[String]].map({ d => dateFormatterUTC.parseDateTime(d) }).getOrElse(new DateTime())
    ))

    def writes(obj: Link): JsValue = {
      val edoc: Map[String,JsValue] = Map(
        "id"            -> JsNumber(obj.id.get),
        "url"           -> JsString(obj.url),
        "poster"        -> JsNumber(obj.poster),
        "category"      -> JsString(obj.category),
        "position"      -> JsNumber(obj.position),
        "description"   -> JsString(obj.description),
        "dateCreated"   -> JsString(dateFormatter.print(obj.dateCreated))
      )
      toJson(edoc)
    }
  }

  /**
   * JSON conversion for UserLink
   */
  implicit object UserLinkFormat extends Format[UserLink] {
    def reads(json: JsValue): JsResult[UserLink] = JsSuccess(UserLink(
      id = Id((json \ "id").as[Long]),
      userId = (json \ "userId").as[Option[Long]],
      url = (json \ "url").as[String],
      poster = (json \ "poster").as[Long],
      category = (json \ "category").as[String],
      position = (json \ "position").as[Int],
      description = (json \ "description").as[String],
      dateCreated = (json \ "dateCreated").as[Option[String]].map({ d => dateFormatterUTC.parseDateTime(d) }).getOrElse(new DateTime())
    ))

    def writes(obj: UserLink): JsValue = {
      val edoc: Map[String,JsValue] = Map(
        "id"            -> JsNumber(obj.id.get),
        "read"          -> JsBoolean(obj.read),
        "url"           -> JsString(obj.url),
        "poster"        -> JsNumber(obj.poster),
        "category"      -> JsString(obj.category),
        "position"      -> JsNumber(obj.position),
        "description"   -> JsString(obj.description),
        "dateCreated"  -> JsString(dateFormatter.print(obj.dateCreated))
      )
      toJson(edoc)
    }
  }
}