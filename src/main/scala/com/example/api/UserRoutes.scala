package com.example.api

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import scalaz.{-\/, \/-}

import com.example.repository.{ModelJsonSerializer, UserRepository}

/** User routes factory **/
object UserRoutes {

  /** Create instance
    *
    * @param repository user repository
    * @param system actor system
    * @return user routes instance
    */
  def apply(repository: UserRepository)(implicit system: ActorSystem): UserRoutes =
    new UserRoutes(repository)

}

/** User routes
  *
  * @param repository user repository
  * @param system actor system
  */
class UserRoutes(repository: UserRepository)(implicit system: ActorSystem)
    extends SprayJsonSupport
    with ModelJsonSerializer
    with ApiSupport {

  import com.example.repository.Model._

  implicit val executor: ExecutionContext = system.dispatcher

  /** Routes **/
  val routes: Route = extractRequestContext { implicit cxt =>
    handleExceptions(handleException) {
      pathPrefix("users") {
        pathEndOrSingleSlash {
          get {
            onSuccess(repository.getAll) {
              case \/-(res) => complete(StatusCodes.OK -> res)
              case -\/(ex)  => throw ex
            }
          } ~
            post {
              entity(as[User]) { req =>
                onSuccess(repository.add(req)) {
                  case \/-(res) => complete(StatusCodes.Created -> res.toString)
                  case -\/(ex)  => throw ex
                }
              }
            }
        } ~
          path(IntNumber) { id =>
            get {
              onSuccess(repository.getById(id)) {
                case \/-(Some(res)) => complete(StatusCodes.OK -> res)
                case \/-(None)      => complete(StatusCodes.NotFound)
                case -\/(ex)        => throw ex
              }
            } ~
              put {
                entity(as[User]) { req =>
                  onSuccess(repository.update(req)) {
                    case \/-(_)  => complete(StatusCodes.NoContent)
                    case -\/(ex) => throw ex
                  }
                }
              } ~
              delete {
                onSuccess(repository.delete(id)) {
                  case \/-(_)  => complete(StatusCodes.NoContent)
                  case -\/(ex) => throw ex
                }
              }
          }
      }
    }
  }

}
