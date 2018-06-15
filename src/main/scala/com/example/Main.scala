package com.example

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.io.StdIn

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import com.example.api.UserRoutes
import com.example.infrastructure.MySQLDBComponent
import com.example.repository.UserRepositoryImpl

object Main extends App {

  val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference(), "akka")
  config.checkValid(ConfigFactory.defaultReference(), "http")
  config.checkValid(ConfigFactory.defaultReference(), "slick")

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executor: ExecutionContext = system.dispatcher

  private val userRepository = new UserRepositoryImpl with MySQLDBComponent

  private val userRoutes = UserRoutes(userRepository)

  private val routes = userRoutes.routes

  private val bindingFuture =
    Http().bindAndHandle(routes, config.getString("http.hostname"), config.getInt("http.port"))

  bindingFuture.onComplete {
    case util.Success(_) => println("started server.")
    case util.Failure(ex) =>
      system.log.error(s"failed start server. reason: ${ex.getMessage}")
      sys.exit(1)
  }

  Await.ready(
    bindingFuture.flatMap { _ =>
      val promise = Promise[Done]()

      sys.addShutdownHook {
        promise.trySuccess(Done)
        ()
      }

      Future {
        blocking {
          if (StdIn.readLine("press RETURN to stop...\n") != null) {
            promise.trySuccess(Done)
          }
        }
      }

      promise.future
    },
    Duration.Inf
  )

  bindingFuture.flatMap(_.unbind()).onComplete { _ =>
    println("stopped server.")
    system.terminate()
  }

}
