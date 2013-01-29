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
   * JSON conversion for Link
   */
  implicit object LinkFormat extends Format[Link] {
    def reads(json: JsValue): Link = Link(
      id = Id((json \ "id").as[Long]),
      url = (json \ "url").as[String],
      poster = (json \ "poster").as[Long],
      org = (json \ "org").as[Long],
      position = (json \ "position").as[Int],
      description = (json \ "description").as[String],
      dateCreated = (json \ "date_created").as[Option[String]].map({ d => dateFormatterUTC.parseDateTime(d) }).getOrElse(new DateTime())
    )

    def writes(obj: Link): JsValue = {
      val edoc: Map[String,JsValue] = Map(
        "id"            -> JsNumber(obj.id.get),
        "url"           -> JsString(obj.url),
        "poster"        -> JsNumber(obj.poster),
        "org"           -> JsNumber(obj.org),
        "position"      -> JsNumber(obj.position),
        "description"   -> JsString(obj.description),
        "date_created"  -> JsString(dateFormatter.print(obj.dateCreated))
      )
      toJson(edoc)
    }
  }
}