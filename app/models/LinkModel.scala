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
 * Class for a category.
 */
case class Category(
  name: String
)

/**
 * Class for a link.
 */
case class Link(
  id: Pk[Long] = NotAssigned,
  url: String,
  poster: Long,
  category: String,
  position: Long,
  description: String,
  dateCreated: DateTime
)

/**
 * Class for a user link.
 */
case class UserLink(
  id: Pk[Long] = NotAssigned,
  userId: Option[Long],
  url: String,
  poster: Long,
  category: String,
  position: Long,
  description: String,
  dateCreated: DateTime
) {
  def read = this.userId.isDefined
}

object LinkModel {

  val allQuery = SQL("SELECT * FROM links ORDER BY position")
  val allForUserQuery = SQL("SELECT * FROM links AS l LEFT JOIN user_links AS ul ON l.id = ul.link_id WHERE (ul.user_id = {user_id} OR ul.user_id IS NULL)")
  val allCategoriesQuery = SQL("SELECT DISTINCT(category) FROM links ORDER BY category")
  val getByIdQuery = SQL("SELECT * FROM links WHERE id={id}")
  val getByIdForUserQuery = SQL("SELECT * FROM links AS l LEFT JOIN user_links AS ul ON l.id = ul.link_id WHERE (ul.user_id = {user_id} OR ul.user_id IS NULL) AND l.id={link_id}")
  val listQuery = SQL("SELECT * FROM links ORDER BY position LIMIT {offset},{count}")
  val listCountQuery = SQL("SELECT COUNT(*) FROM links")
  val insertQuery = SQL("INSERT INTO links (url, poster, category, position, description, date_created) VALUES ({url}, {poster}, {category}, {position}, {description}, UTC_TIMESTAMP())")
  val updateQuery = SQL("UPDATE links SET url={url}, poster={poster}, category={category}, position={position}, description={description} WHERE id={id}")
  val deleteQuery = SQL("DELETE FROM links WHERE id={id}")
  val readQuery = SQL("INSERT IGNORE INTO user_links (user_id, link_id, date_created) VALUES ({user_id}, {link_id}, UTC_TIMESTAMP)")
  val unreadQuery = SQL("DELETE FROM user_links WHERE link_id={link_id} AND user_id={user_id}")

  val category = {
    get[String]("links.category") map { case name => Category(name) }
  }

  // parser for retrieving a link
  val link = {
    get[Pk[Long]]("id") ~
    get[String]("url") ~
    get[Long]("poster") ~
    get[String]("category") ~
    get[Long]("position") ~
    get[String]("description") ~
    get[DateTime]("date_created") map {
      case id~url~poster~category~position~description~dateCreated => Link(id, url, poster, category, position, description, dateCreated)
    }
  }

  // parser for retrieving a user link
  val userLink = {
    get[Pk[Long]]("id") ~
    get[Option[Long]]("ul.user_id") ~
    get[String]("url") ~
    get[Long]("poster") ~
    get[String]("category") ~
    get[Long]("position") ~
    get[String]("description") ~
    get[DateTime]("l.date_created") map {
      case id~userId~url~poster~category~position~description~dateCreated => UserLink(id, userId, url, poster, category, position, description, dateCreated)
    }
  }

  /**
   * Create a link.
   */
  def create(link: Link): Option[Link] = {

    val id = DB.withConnection { implicit conn =>
      insertQuery.on(
        'url        -> link.url,
        'poster     -> link.poster,
        'category   -> link.category,
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

  /**
   * Retrieve a link by id for a user.
   */
  def getByIdForUser(linkId: Long, userId: Long) : Option[UserLink] = {

    DB.withConnection { implicit conn =>
      getByIdForUserQuery.on('user_id -> userId, 'link_id -> linkId).as(userLink.singleOpt)
    }
  }

  /**
   * Get ALL THE LINKS
   */
  def getAll: List[Link] = {
    DB.withConnection { implicit conn =>
      allQuery.as(link *)
    }
  }

  /**
   * Get all categories.
   */
  def getAllCategories: List[Category] = {
    DB.withConnection { implicit conn =>
      allCategoriesQuery.as(category *)
    }
  }

  /**
   * Get all the links for a user
   */
  def getAllForUser(userId: Long): List[UserLink] = {

    DB.withConnection { implicit conn =>
      allForUserQuery.on('user_id -> userId).as(userLink *)
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
   * Mark a link as read by a user.
   */
   def read(id: Long, userId: Long): Option[UserLink] = {
    DB.withConnection { implicit conn =>
      val ulid = readQuery.on(
        'link_id -> id,
        'user_id -> userId
      ).executeInsert()
      getByIdForUser(id, userId)
    }
   }

  /**
   * Unmark a link as read by a user.
   */
   def unread(id: Long, userId: Long): Option[UserLink] = {
    DB.withConnection { implicit conn =>
      getByIdForUser(id, userId).map({ l =>
        unreadQuery.on(
          'link_id -> id,
          'user_id -> userId
        ).execute()
        l
      })
    }
   }

  /**
   * Update a link. Returns the changed link.
   */
  def update(id: Long, link: Link): Option[Link] = {

    DB.withConnection { implicit conn =>
      updateQuery.on(
        'id         -> id,
        'url        -> link.url,
        'poster     -> link.poster,
        'category   -> link.category,
        'position   -> link.position,
        'description-> link.description
      ).execute
      getById(id)
    }
  }
}
