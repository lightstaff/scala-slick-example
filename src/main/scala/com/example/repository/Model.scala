package com.example.repository

/** Model **/
object Model {

  /** User
    *
    * @param id identity
    * @param name name
    */
  final case class User(id: Option[Int], name: String)

}
