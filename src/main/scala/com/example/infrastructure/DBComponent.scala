package com.example.infrastructure

import slick.jdbc.JdbcProfile

/** Interface for database component **/
trait DBComponent {

  /** Jdbc profile **/
  protected val profile: JdbcProfile

  import profile.api._

  /** Database **/
  protected def db: Database

}
