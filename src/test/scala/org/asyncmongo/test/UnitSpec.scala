package org.asyncmongo.test

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.concurrent.ScalaFutures

import org.asyncmongo.dao.ReactiveContext

class UnitSpec extends FlatSpec with Matchers with OptionValues with Inside with BeforeAndAfter with ScalaFutures with Eventually {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))
  implicit val reactiveContext = ReactiveContext("localhost", "reactive-test", "People")

  def await[T](awaitable: Awaitable[T]) = {
    Await.result(awaitable, 2000 millis)
  }
}
