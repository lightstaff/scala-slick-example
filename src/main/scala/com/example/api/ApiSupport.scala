package com.example.api

import scala.util.control.NonFatal

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RequestContext}

/** Api support **/
trait ApiSupport {

  private def logException(ctx: RequestContext, message: String): Unit =
    ctx.log.error(
      s"failed request. uri: ${ctx.request.getUri}, method: ${ctx.request.method.value}, reason: $message")

  /** handle for exception
    *
    * @param ctx request context
    * @return Exception handler
    */
  def handleException(implicit ctx: RequestContext): ExceptionHandler = ExceptionHandler {
    case NonFatal(ex) =>
      logException(ctx, s"unknown error. ${ex.getMessage}")
      complete(StatusCodes.InternalServerError -> ex.getMessage)
  }

}
