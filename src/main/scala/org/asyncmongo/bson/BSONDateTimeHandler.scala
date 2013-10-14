package org.asyncmongo.bson

import reactivemongo.bson.{BSONDateTime, BSONHandler}

import java.util.Date

/**
 * Converts BSONDateTime to java Date and vice versa
 */
object BSONDateTimeHandler extends BSONHandler[BSONDateTime, Date] {
  def read(time: BSONDateTime) = new Date(time.value)
  def write(time: Date) = BSONDateTime(time.getTime)
}
