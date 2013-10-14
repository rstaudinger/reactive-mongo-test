package org.asyncmongo.dao

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.bson._
import reactivemongo.core.commands.LastError


trait ReactiveDAO[T] {
  /**
   * Insert a new document.
   */
  def insert(doc: T)(implicit reactiveContext: ReactiveContext): Future[LastError]
  /**
   * Update and existing document.
   */
  def save(doc: T)(implicit reactiveContext: ReactiveContext): Future[LastError]

  /**
   * Delete a document.
   */
  def remove(doc: T)(implicit reactiveContext: ReactiveContext): Future[LastError]

  /**
   * Find a document by its _id field.
   */
  def findById(id: BSONObjectID)(implicit reactiveContext: ReactiveContext): Future[Option[T]]

  /**
   * Find documents matching a query provided as BSONDocument.
   */
  def findMany(query: BSONDocument)(implicit reactiveContext: ReactiveContext): Future[List[T]]

  /**
   * Find document matching a query provided as tuples.  The resulting query will and the tuples together.
   */
  def findMany(filters: (String, String)*)(implicit reactiveContext: ReactiveContext): Future[List[T]]
}

/**
 * Base class for all DAO objects.  Defines basic CRUD methods. Needs BSON reader and writer in scope for type T.
 */
class GenericDAO[T](implicit reader: BSONDocumentReader[T], writer: BSONDocumentWriter[T]) extends ReactiveDAO[T] {

  /**
   * Insert a new document.
   */
  override def insert(doc: T)(implicit reactiveContext: ReactiveContext): Future[LastError]  = {
    reactiveContext.collection.insert(doc)
  }

  /**
   * Update and existing document.
   */
  override def save(doc: T)(implicit reactiveContext: ReactiveContext): Future[LastError] = {
    reactiveContext.collection.save(doc)
  }

  /**
   * Delete a document.
   */
  override def remove(doc: T)(implicit reactiveContext: ReactiveContext): Future[LastError] = {
    reactiveContext.collection.remove(doc)
  }

  /**
   * Find a document by its _id field.
   */
  override def findById(id: BSONObjectID)(implicit reactiveContext: ReactiveContext): Future[Option[T]] = {
    val query = BSONDocument("_id" -> id)
    reactiveContext.collection.find(query).one[T]
  }

  /**
   * Find documents matching a query provided as BSONDocument.
   */
  override def findMany(query: BSONDocument)(implicit reactiveContext: ReactiveContext): Future[List[T]] = {
    reactiveContext.collection.find(query).cursor[T].toList
  }

  /**
   * Find document matching a query provided as tuples.  The resulting query will and the tuples together.
   */
  override def findMany(filters: (String, String)*)(implicit reactiveContext: ReactiveContext): Future[List[T]] = {
    val query = filters.map { t => (t._1, BSONString(t._2)) }
    findMany(BSONDocument(query))
  }
}


/**
 * Generic DAO for use if only standard methods are necessary.  Needs BSON reader and writer in scope for type T.
 *
 * Usage:
 *   GenericDAO[Person].findMany(...)
 */
//class GenericDAO[T](implicit reader: BSONDocumentReader[T], writer: BSONDocumentWriter[T]) extends BaseDAO[T]

object GenericDAO  {
  def apply[T]()(implicit reader: BSONDocumentReader[T], writer: BSONDocumentWriter[T]) = {
    new GenericDAO[T]()
  }
}

