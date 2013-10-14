
import java.util.Date

import reactivemongo.bson.{BSONObjectID, BSON, BSONDocument}

import org.asyncmongo.dao.{GenericDAO, ReactiveContext}
import org.asyncmongo.model.{Person, Name, PersonDAO}

import scala.concurrent.ExecutionContext.Implicits.global


object Main extends App {
  implicit val reactiveContext = ReactiveContext("localhost", "reactive-test", "People")

  //insert
  findByIdAndUpdate
  findMany
  findManyWithForComp
  findManyWithForeach
  findAndDelete


  def insert() {
    val me = Person(username="rstaudinger", age=Some(42), name=Name("Reinhold", "Staudinger"))
    val somebody = Person(username="somebody", age=Some(42), name=Name("Some", "Body", false))
    Person.DAO.insert(me)
    Person.DAO.insert(somebody)
  }

  def findByIdAndUpdate() {
    // find single person ...
    val personFuture = Person.DAO.findById(new BSONObjectID("525586349d134301005c1d87"))

    // ... do something different whether a match was found or not
    personFuture onSuccess {
      case None => println("Person with ID does not exist!")
      case Some(p) => {
        println("Found single person before change: " + BSONDocument.pretty(BSON.writeDocument(p)))
        // update dob
        Person.DAO.save(p.copy(dateOfBirth = Some(new Date())))
      }
    }

    // ... exception handling
    personFuture onFailure {
      case e => println("Encountered unexpected error: " + e.getMessage)
    }

    val updated = Person.DAO.findById(new BSONObjectID("525586349d134301005c1d87"))
    updated onSuccess {
      case Some(p) => println("Found single person after change: " + BSONDocument.pretty(BSON.writeDocument(p)))
    }
    updated onFailure {
      case e => println("Encountered unexpected error: " + e.getMessage)
    }
  }

  def findMany() {
    // find many people ...
    val findFuture = Person.DAO.findMany("username" -> "rstaudinger")
    // ... do something different whether any match was found or not
    findFuture onSuccess {
      case Nil => println("Found no people matching query!")
      case people =>  println("Found " + people.length + " people matching query" )
    }
    // ... exception handling
    findFuture onFailure {
      case e => println("Encountered unexpected error: " + e.getMessage)
    }
  }

  def findManyWithForComp() {
    // use for comprehension to yield aspects of the list
    val findFuture2 = Person.DAO.findMany("username" -> "rstaudinger")
    val count = for {
      maybeList <- findFuture2
    } yield maybeList.length

    count onSuccess {
      case c: Int => println("Found " + c + " people with findMany and for comprehension")
    }
  }

  def findManyWithForeach() {
    // do something with all matching documents
    Person.DAO.findMany("username" -> "somebody").map { list =>
      list match {
        case Nil => println("Found no people matching query!")
        case people => people.foreach { p =>
          println("Found person in list.foreach: " + BSONDocument.pretty(BSON.writeDocument(p)))
        }
      }
    }
  }

  def findAndDelete() {
    // use for comprehension to yield last in the list
    val toDelete = for {
      people <- GenericDAO[Person].findMany("username" -> "somebody")
    } yield people.last

    // delete the found person
    toDelete onSuccess {
      case person => {
        println("Deleting id: " + person._id.toString)
        Person.DAO.remove(person)
      }
    }
  }
}
