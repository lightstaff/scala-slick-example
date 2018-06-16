package com.example.respository

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scalaz.{-\/, \/-}

import com.example.infrastructure.H2DBComponent
import com.example.repository.UserRepositoryImplSelfType

class UserRepositoryImplSelfTypeH2Spec
    extends WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures {

  import com.example.repository.Model._

  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = system.dispatcher

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(200, Millis))

  private val repository = new UserRepositoryImplSelfType with H2DBComponent

  override def afterAll(): Unit = {
    system.terminate()
    ()
  }

  "user repository" should {

    "success pattern" when {

      "crud" in {

        var user = User(None, "test")

        repository.add(User(None, "test")).futureValue match {
          case \/-(res) => user = user.copy(id = Option(res))
          case -\/(ex)  => throw ex
        }

        repository.getAll.futureValue match {
          case \/-(res) =>
            res.size shouldBe 1
            res.head shouldBe user
          case -\/(ex) => throw ex
        }

        repository.getById(user.id.getOrElse(0)).futureValue match {
          case \/-(Some(res)) => res shouldBe user
          case \/-(None)      => throw new Exception("Not Found!!")
          case -\/(ex)        => throw ex
        }

        user = user.copy(name = "update")

        repository.update(user).futureValue match {
          case \/-(res) => res shouldBe 1
          case -\/(ex)  => throw ex
        }

        repository.getById(user.id.getOrElse(0)).futureValue match {
          case \/-(Some(res)) => res shouldBe user
          case \/-(None)      => throw new Exception("Not Found!!")
          case -\/(ex)        => throw ex
        }

        repository.delete(user.id.getOrElse(0)).futureValue match {
          case \/-(res) => res shouldBe 1
          case -\/(ex)  => throw ex
        }

        repository.getById(user.id.getOrElse(0)).futureValue match {
          case \/-(Some(_)) => throw new Exception("Not Deleted!!")
          case \/-(None)    =>
          case -\/(ex)      => throw ex
        }

      }

    }

  }
}
