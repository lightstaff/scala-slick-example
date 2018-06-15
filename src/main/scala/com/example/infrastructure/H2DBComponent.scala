package com.example.infrastructure

import com.typesafe.config.ConfigFactory
import slick.jdbc.H2Profile.api._
import slick.jdbc.{H2Profile, JdbcProfile}

/** Database component for H2 **/
trait H2DBComponent extends DBComponent {

  /** Jdbc profile **/
  override protected val profile: JdbcProfile = H2Profile

  import profile.api._

  /** Database instance **/
  override protected lazy val db: Database = H2DBConnector.connection

}

/** Singleton database connector for H2 **/
private object H2DBConnector {

  private val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference(), "slick.h2")

  /** Connection **/
  val connection = Database.forConfig("slick.h2", config)

}
