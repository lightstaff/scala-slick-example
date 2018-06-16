package com.example.infrastructure

import slick.jdbc.JdbcProfile

/** Interface for database component **/
trait DBComponent {

  /** Jdbc profile **/
  val profile: JdbcProfile

  import profile.api._

  /** Database **/
  def db: Database

}
