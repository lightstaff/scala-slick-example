package com.example.infrastructure

import com.typesafe.config.ConfigFactory
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.{JdbcProfile, MySQLProfile}

/** Database component for MySQL **/
trait MySQLDBComponent extends DBComponent {

  /** Jdbc profile **/
  override protected val profile: JdbcProfile = MySQLProfile

  import profile.api._

  /** Database instance **/
  override protected lazy val db: Database = MySQLDBConnector.connection

}

/** Singleton database connector for MySQL **/
private object MySQLDBConnector {

  private val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference(), "slick.mysql")

  /** Connection **/
  val connection = Database.forConfig("slick.mysql", config)

}
