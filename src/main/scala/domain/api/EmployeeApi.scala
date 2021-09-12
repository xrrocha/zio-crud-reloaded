package net.xrrocha.ziocrud.domain.api

import net.xrrocha.ziocrud.domain.model.EmployeeRepository.{EmployeeRepository, PersistenceFailure}
import net.xrrocha.ziocrud.domain.model.{Employee, EmployeeRepository}
import zio.ZIO

object EmployeeApi:

  import BusinessError.*
  import ValidationError.*

  def create(req: CreateEmployeeRequest): ZIO[EmployeeRepository, Any, Employee] =
    Employee(req.firstName, req.lastName) match
      case None => ZIO.fail(InvalidInput(req.firstName, req.lastName))
      case Some(employee) =>
        EmployeeRepository.get(employee.id).flatMap {
          case Some(_) => ZIO.fail(EmployeeAlreadyExists(employee.id))
          case None => EmployeeRepository.save(employee)
        }

  def update(req: UpdateEmployeeRequest): ZIO[EmployeeRepository, Any, Employee] =
    for
      oldVersion <-
        if req.firstName.isEmpty || req.lastName.isEmpty then
          ZIO.fail(InvalidInput(req.firstName, req.lastName))
        else
          EmployeeRepository.get(req.id).flatMap {
            case Some(employee) => ZIO.succeed(employee)
            case None => ZIO.fail(EmployeeDoesNotExists(req.id))
          }
      result <- EmployeeRepository.save(oldVersion.copy(firstName = req.firstName, lastName = req.lastName))
    yield result

  def getAll(): ZIO[EmployeeRepository, PersistenceFailure, Seq[Employee]] = EmployeeRepository.getAll()

  def get(id: String): ZIO[EmployeeRepository, PersistenceFailure, Option[Employee]] = EmployeeRepository.get(id)

  def delete(id: String): ZIO[EmployeeRepository, PersistenceFailure, Unit] = EmployeeRepository.delete(id)

  case class CreateEmployeeRequest(firstName: String, lastName: String)

  case class UpdateEmployeeRequest(id: String, firstName: String, lastName: String)

  enum ValidationError:
    case InvalidInput(firstName: String, lastName: String) extends ValidationError

  enum BusinessError:
    case EmployeeAlreadyExists(id: String) extends BusinessError
    case EmployeeDoesNotExists(id: String) extends BusinessError

