package org.asyncmongo.model

import java.util.Date

import reactivemongo.bson._

import org.asyncmongo.dao.GenericDAO
import org.asyncmongo.bson.BSONDateTimeHandler


case class Name(
  first: String,
  last: String,
  ltr: Boolean = false
)

object Name {
  // handler will be generated at compile time through macro
  implicit val nameHandler = Macros.handler[Name]
}


case class Person(
  _id: BSONObjectID = BSONObjectID.generate,  // reactive doesn't have a way to map fields to _id, so need to use exact name
  username: String,
  name: Name,
  age: Option[Int],
  dateOfBirth: Option[Date] = None
)

object Person {
  // need to have implicit dataTime handler in scope to use date fields.  could be made global
  implicit val dateHandler = BSONDateTimeHandler

  // handler will be generated at compile time through macro
  implicit val personHandler = Macros.handler[Person]

  val DAO = new PersonDAO()
}


class PersonDAO extends GenericDAO[Person] {
  // inherits a set of standard dao methods, custom methods for person go here
  // if there is no need for custom methods, use GenericDAO
}

