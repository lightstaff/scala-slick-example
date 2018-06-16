package com.example.repository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

import scalaz.\/
import scalaz.syntax.ToEitherOps

import com.example.infrastructure.DBComponent

/** User repository implement dependency injection factory **/
object UserRepositoryImplDI {

  def apply(dbComponent: DBComponent): UserRepositoryImplDI = new UserRepositoryImplDI(dbComponent)

}

/** User repository implement dependency injection
  *
  * @param dbComponent database component
  */
class UserRepositoryImplDI(dbComponent: DBComponent) extends UserRepository with ToEitherOps {

  import dbComponent.profile.api._

  import Model._

  private val users = TableQuery[UserTable]

  /** Get all
    *
    * @return Sequence in user
    */
  override def getAll(implicit ec: ExecutionContext): Future[\/[Throwable, Seq[User]]] =
    dbComponent.db.run(users.result).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Seq[User]]
    }

  /** Get by id
    *
    * @param id identity
    * @return Optional in user
    */
  override def getById(id: Int)(
      implicit ec: ExecutionContext): Future[\/[Throwable, Option[User]]] = {
    val q = for {
      u <- users if u.id === id
    } yield u

    dbComponent.db.run(q.result.headOption).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Option[User]]
    }
  }

  /** Add user
    *
    * @param user user
    * @return created id
    */
  override def add(user: User)(implicit ec: ExecutionContext): Future[\/[Throwable, Int]] =
    dbComponent.db.run(users returning users.map(_.id) += user).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Int]
    }

  /** Update user
    *
    * @param user user
    * @return update count
    */
  override def update(user: User)(implicit ec: ExecutionContext): Future[\/[Throwable, Int]] = {
    val q = for {
      u <- users if u.id === user.id
    } yield u.name

    dbComponent.db.run(q.update(user.name)).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Int]
    }
  }

  /** Delete user
    *
    * @param id identity
    * @return delete count
    */
  override def delete(id: Int)(implicit ec: ExecutionContext): Future[\/[Throwable, Int]] = {
    val q = for {
      u <- users if u.id === id
    } yield u

    dbComponent.db.run(q.delete).map(_.right[Throwable]).recover {
      case NonFatal(ex) => ex.left[Int]
    }
  }

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    override def * = (id.?, name) <> (User.tupled, User.unapply)

  }

}
