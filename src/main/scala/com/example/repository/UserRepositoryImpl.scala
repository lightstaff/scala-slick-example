package com.example.repository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

import scalaz.\/
import scalaz.syntax.ToEitherOps

import com.example.infrastructure.DBComponent

/** User repository implement **/
trait UserRepositoryImpl extends UserRepository with ToEitherOps {
  this: DBComponent =>

  import profile.api._

  import Model._

  private val users = TableQuery[UserTable]

  /** Get all
    *
    * @return Sequence in user
    */
  override def getAll(implicit ex: ExecutionContext): Future[\/[Throwable, Seq[User]]] =
    db.run(users.result).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Seq[User]]
    }

  /** Get by id
    *
    * @param id identity
    * @return Optional in user
    */
  override def getById(id: Int)(
      implicit ex: ExecutionContext): Future[\/[Throwable, Option[User]]] = {
    val q = for {
      u <- users if u.id === id
    } yield u

    db.run(q.result.headOption).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Option[User]]
    }
  }

  /** Add user
    *
    * @param user user
    * @return created id
    */
  override def add(user: User)(implicit ex: ExecutionContext): Future[\/[Throwable, Int]] =
    db.run(users returning users.map(_.id) += user).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Int]
    }

  /** Update user
    *
    * @param user user
    * @return update count
    */
  override def update(user: User)(implicit ex: ExecutionContext): Future[\/[Throwable, Int]] = {
    val q = for {
      u <- users if u.id === user.id
    } yield u.name

    db.run(q.update(user.name)).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Int]
    }
  }

  /** Delete user
    *
    * @param id identity
    * @return delete count
    */
  override def delete(id: Int)(implicit ex: ExecutionContext): Future[\/[Throwable, Int]] = {
    val q = for {
      u <- users if u.id === id
    } yield u

    db.run(q.delete).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Int]
    }
  }

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    override def * = (id.?, name) <> (User.tupled, User.unapply)

  }

}
