package net.xrrocha.ziocrud.domain.model

import zio.{Has, IO, ZIO}

import java.security.Provider.Service
import scala.util.Try

case class Employee(id: String, firstName: String, lastName: String)

object Employee:
  def apply(firstName: String, lastName: String): Option[Employee] =
    Try(s"${firstName.head}${lastName.substring(0, 5)}".toLowerCase())
      .toOption
      .map(id => Employee(id, firstName, lastName))

object EmployeeRepository:

  type EmployeeRepository = Has[EmployeeRepository.Service]

  trait Service:
    def save(employee: Employee): IO[PersistenceFailure, Employee]

    def get(id: String): IO[PersistenceFailure, Option[Employee]]

    def getAll(): IO[PersistenceFailure, Seq[Employee]]

    def delete(id: String): IO[PersistenceFailure, Unit]

  enum PersistenceFailure:
    case UnexpectedPersistenceFailure(err: Throwable) extends PersistenceFailure

  def save(employee: Employee): ZIO[EmployeeRepository, PersistenceFailure, Employee] = ZIO.accessZIO(_.get.save(employee))

  def get(id: String): ZIO[EmployeeRepository, PersistenceFailure, Option[Employee]] = ZIO.accessZIO(_.get.get(id))

  def getAll(): ZIO[EmployeeRepository, PersistenceFailure, Seq[Employee]] = ZIO.accessZIO(_.get.getAll())

  def delete(id: String): ZIO[EmployeeRepository, PersistenceFailure, Unit] = ZIO.accessZIO(_.get.delete(id))

