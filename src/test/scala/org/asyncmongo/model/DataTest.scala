package org.asyncmongo.model

import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.bson.BSONObjectID

import org.asyncmongo.test.UnitSpec

/**
 * Test with an actual mongo instance.  Require a mongo instance matching the connection params
 * passed to reactiveContext.
 */
class DataTest extends UnitSpec {

  val harry = Person(username="hpotter", age=Some(42), name=Name("Harry", "Potter"))
  val ron = Person(username="rweasly", age=Some(42), name=Name("Ron", "Weasly", false))

  before {
    // setup data synchronous
    await { Person.DAO.insert(harry) }
    await { Person.DAO.insert(ron) }
  }

  after {
    // clean up data synchronous
    await { Person.DAO.remove(harry) }
    await { Person.DAO.remove(ron) }
  }

  "findMany with username" should "find match" in {
    val findFuture = Person.DAO.findMany("username" -> "hpotter")
    val count = for {
      maybeList <- findFuture
    } yield maybeList.length

    count onSuccess {
      case c: Int => assert(c == 1)
    }
  }

  "find and update" should "work if document exists" in {
    val dob = new Date()

    // load person synchronous
    val p = await { Person.DAO.findById(harry._id) }
    assert(p != None)

    // update person synchronous
    await { Person.DAO.save(p.get.copy(dateOfBirth = Some(dob))) }

    // find and validate async
    val updatedFuture = Person.DAO.findById(harry._id)
    whenReady(updatedFuture) { updated => updated match {
        case None => fail("Should have found updated person")
        case Some(p) => assert(p.dateOfBirth === Some(dob))
      }
    }
  }

  "find and remove" should "successfully delete person" in {
    // load person synchronous
    val p = await { Person.DAO.findById(harry._id) }
    assert(p != None)

    // remove person synchronous
    await { Person.DAO.remove(p.get) }

    // find and validate async
    val deletedFuture = Person.DAO.findById(harry._id)
    whenReady(deletedFuture) { deleted =>
      assert(deleted === None)
    }
  }

  "find and remove" should "handle non-existing id" in {
    // remove person synchronous
    val future = Person.DAO.remove(harry.copy(_id = BSONObjectID.generate))
    whenReady(future) { le =>
      assert(le.n === 0)
    }

  }

}
