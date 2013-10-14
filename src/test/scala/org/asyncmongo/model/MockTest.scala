package org.asyncmongo.model

import java.util.Date

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.core.commands.LastError

import org.scalatest.mock.MockitoSugar
import org.scalatest.ShouldMatchers

import org.mockito.Mockito._

import org.asyncmongo.test.UnitSpec
import org.asyncmongo.dao.GenericDAO


/**
 * A few contrived tests with mocked objects and futures.
 */
class MockTest extends UnitSpec with MockitoSugar with ShouldMatchers {

  val mockGenericDAO = mock[GenericDAO[Person]]
  val mockPersonDAO = mock[PersonDAO]

  val dob = new Date()
  val harry = Person(username="hpotter", age=Some(42), name=Name("Harry", "Potter"))
  val ron = Person(username="rweasly", age=Some(42), name=Name("Ron", "Weasly", false))

  before {
    val lastError = new LastError(true, None, Some(1), None, None, 1, true)
    when(mockPersonDAO.findById(harry._id)).thenReturn(Future(Some(harry)))
    when(mockPersonDAO.remove(harry)).thenReturn(Future(lastError))

    when(mockGenericDAO.findById(ron._id)).thenReturn(Future(Some(ron)))
    when(mockGenericDAO.save(ron.copy(dateOfBirth = Some(dob)))).thenReturn(Future(lastError))
  }

  after {
    reset(mockPersonDAO)
    reset(mockGenericDAO)
  }

  "find and remove with PersonDAO" should "successfully delete person" in {
    // load person synchronous
    val p = await { mockPersonDAO.findById(harry._id) }
    p should be(Some(harry))

    // remove person synchronous
    await { mockPersonDAO.remove(p.get) }

    when(mockPersonDAO.findById(harry._id)).thenReturn(Future(None))
    val deletedFuture = Person.DAO.findById(harry._id)
    whenReady(deletedFuture) { deleted =>
      deleted should be(None)
    }
    verify(mockPersonDAO).findById(harry._id)
    verify(mockPersonDAO).remove(harry)
  }

  "find and update with GenericDAO" should "successfully update person" in {
    // load person synchronous
    val p = await { mockGenericDAO.findById(ron._id) }
    p should be(Some(ron))

    // update and save
    val updateFuture = mockGenericDAO.save(p.get.copy(dateOfBirth = Some(dob)))

    whenReady(updateFuture) { updated =>
      updated.code should be(Some(1))
    }

    verify(mockGenericDAO).findById(ron._id)
    verify(mockGenericDAO).save(ron.copy(dateOfBirth = Some(dob)))
  }

}
