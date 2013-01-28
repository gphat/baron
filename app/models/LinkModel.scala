package models

import anorm._
import anorm.SqlParser._
import util.AnormExtension._
import util.Pagination._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current
import play.Logger

/**
 * Class for users in a group.
 */
case class Link(
  id: Pk[Long] = NotAssigned,
  url: String,
  poster: Long,
  org: Long,
  position: Int,
  description: String,
  dateCreated: DateTime
)

object LinkModel {

  val allQuery = SQL("SELECT * FROM links")
  val getByIdQuery = SQL("SELECT * FROM links WHERE id={id}")
  val listQuery = SQL("SELECT * FROM links ORDER BY position LIMIT {offset},{count}")
  val listCountQuery = SQL("SELECT COUNT(*) FROM links")
  val insertQuery = SQL("INSERT INTO links (url, poster, org, position, description, date_created) VALUES ({name}, {poster}, {org}, {position}, {description}, UTC_TIMESTAMP())")
  val updateQuery = SQL("UPDATE links SET url={url}, poster={poster}, org={org}, position={position}, description={description} WHERE id={id}")
  val deleteQuery = SQL("DELETE FROM links WHERE id={id}")

  // parser for retrieving a group
  val link = {
    get[Pk[Long]]("id") ~
    get[String]("url") ~
    get[Long]("poster") ~
    get[Long]("org") ~
    get[Int]("position") ~
    get[String]("description") ~
    get[DateTime]("date_created") map {
      case id~url~poster~org~position~description~dateCreated => Link(id, url, poster, org, position, description, dateCreated)
    }
  }

  /**
   * Create a group.
   */
  def create(link: Link): Option[Link] = {

    val id = DB.withConnection { implicit conn =>
      insertQuery.on(
        'url        -> link.url,
        'poster     -> link.poster,
        'org        -> link.org,
        'position   -> link.position,
        'description-> link.description
      ).executeInsert()
    }
    getById(id.get)
  }

  /**
   * Delete a link.
   */
  def delete(id: Long): Option[Link] = {
    getById(id).map({ link =>

      DB.withConnection { implicit conn =>
        deleteQuery.on(
          'id -> link.id.get
        ).execute
      }
      Some(link)
    }).getOrElse(None)
  }

  /**
   * Retrieve a link by id.
   */
  def getById(id: Long) : Option[Link] = {

    DB.withConnection { implicit conn =>
      getByIdQuery.on('id -> id).as(link.singleOpt)
    }
  }

  def getAll: List[Link] = {

    DB.withConnection { implicit conn =>
      allQuery.as(link *)
    }
  }

  def list(page: Int = 1, count: Int = 10) : Page[Link] = {

      val offset = count * (page - 1)

      DB.withConnection { implicit conn =>
        val links = listQuery.on(
          'count  -> count,
          'offset -> offset
        ).as(link *)

        val totalRows = listCountQuery.as(scalar[Long].single)

        Page(links, page, count, totalRows)
      }
  }

  /**
   * Update a group.  Returns the changed group.
   */
  def update(id: Long, link: Link): Option[Link] = {

    DB.withConnection { implicit conn =>
      updateQuery.on(
        'id         -> id,
        'url        -> link.url,
        'poster     -> link.poster,
        'org        -> link.org,
        'position   -> link.position,
        'description-> link.description
      ).execute
      getById(id)
    }
  }
}
