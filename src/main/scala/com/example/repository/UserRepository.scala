package com.example.repository

import scala.concurrent.{ExecutionContext, Future}

import scalaz.\/

/** User repository interface **/
trait UserRepository {

  import Model._

  /** Get all
    *
    * @return Sequence in user
    */
  def getAll(implicit ex: ExecutionContext): Future[\/[Throwable, Seq[User]]]

  /** Get by id
    *
    * @param id identity
    * @return Optional in user
    */
  def getById(id: Int)(implicit ex: ExecutionContext): Future[\/[Throwable, Option[User]]]

  /** Add user
    *
    * @param user user
    * @return created id
    */
  def add(user: User)(implicit ex: ExecutionContext): Future[\/[Throwable, Int]]

  /** Update user
    *
    * @param user user
    * @return update count
    */
  def update(user: User)(implicit ex: ExecutionContext): Future[\/[Throwable, Int]]

  /** Delete user
    *
    * @param id identity
    * @return delete count
    */
  def delete(id: Int)(implicit ex: ExecutionContext): Future[\/[Throwable, Int]]

}
