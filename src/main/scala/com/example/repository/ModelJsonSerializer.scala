package com.example.repository

import spray.json._

/** Model json serializer **/
trait ModelJsonSerializer extends DefaultJsonProtocol {

  import Model._

  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)

}
