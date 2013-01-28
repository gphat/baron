package util

import org.joda.time._
import org.joda.time.format._
import anorm._

object AnormExtension {

  val dateFormatGeneration = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC()

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime).withZone(DateTimeZone.UTC))
      case d: java.sql.Date => Right(new DateTime(d.getTime).withZone(DateTimeZone.UTC))
      case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str).withZone(DateTimeZone.UTC))
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass) )
    }
  }
  implicit val dateTimeToStatement = new ToStatement[DateTime] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime): Unit = {
      if(aValue == null) {
        s.setTimestamp(index, null)
      } else {
        s.setTimestamp(index, new java.sql.Timestamp(aValue.withZone(DateTimeZone.UTC).withMillisOfSecond(0).getMillis()) )
      }
    }
  }
}