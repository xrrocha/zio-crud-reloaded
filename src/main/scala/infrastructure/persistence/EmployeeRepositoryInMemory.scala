package net.xrrocha.ziocrud.infrastructure.persistence

import net.xrrocha.ziocrud.domain.model.EmployeeRepository.PersistenceFailure.*
import net.xrrocha.ziocrud.domain.model.{Employee, EmployeeRepository}
import zio.IO

import scala.collection.mutable

object EmployeeRepositoryInMemory extends EmployeeRepository.Service :

  private val db = mutable.Map[String, Employee]()

  override def save(employee: Employee) =
    IO {
      db(employee.id) = employee
      employee
    }
      .mapError(UnexpectedPersistenceFailure(_))

  override def get(id: String): IO[EmployeeRepository.PersistenceFailure, Option[Employee]] =
    IO(db.get(id))
      .mapError(UnexpectedPersistenceFailure(_))

  override def getAll(): IO[EmployeeRepository.PersistenceFailure, Seq[Employee]] =
    IO(db.values.toSeq.sortBy(_.id))
      .mapError(UnexpectedPersistenceFailure(_))

  override def delete(id: String): IO[EmployeeRepository.PersistenceFailure, Unit] =
    IO {
      db -= id
      ()
    }
      .mapError(UnexpectedPersistenceFailure(_))

