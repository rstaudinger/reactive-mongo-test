package org.asyncmongo.dao

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.MongoDriver

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Basic context for Reactive Mongo connection to a single collection.
 *
 * @param hostName the host name for the connection
 * @param dbName the database for the connection
 * @param collectionName the collection for the connection
 */
case class ReactiveContext(private val hostName: String, private val dbName: String, private val collectionName: String) {
  private val driver = new MongoDriver
  private val conn = driver.connection(List(hostName))
  val collection: BSONCollection = conn(dbName).apply(collectionName)
}
