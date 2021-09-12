package net.xrrocha.ziocrud.infrastructure

import net.xrrocha.ziocrud.domain.api.EmployeeApi
import net.xrrocha.ziocrud.domain.api.EmployeeApi.{CreateEmployeeRequest, UpdateEmployeeRequest}
import net.xrrocha.ziocrud.domain.model.Employee
import net.xrrocha.ziocrud.domain.model.EmployeeRepository.EmployeeRepository
import net.xrrocha.ziocrud.infrastructure.Controller.CRUDOperation
import zio.Console.{printLine, readLine}
import zio.{Console, Has, ZIO}

import scala.util.{Failure, Success, Try}

object Controller:

  def menuItems = CRUDOperation.values.map(value => (value.index, value.title))

  def selectOperation(selection: String) = {
    Try(Integer.parseInt(selection)) match
      case Success(idx) => CRUDOperation.values.find(_.index == idx)
      case Failure(_) => None
  }
    .map(_.action)

  enum CRUDOperation[A](val index: Int,
                        val title: String,
                        val action: () => ZIO[Has[Console] with EmployeeRepository, Any, A]):

    case Create extends CRUDOperation(1, "Create new employee", () =>
      for
        _ <- printLine("Please enter employee first name: ")
        firstName <- readLine
        _ <- printLine("Please enter employee last name: ")
        lastName <- readLine
        savedEmployee <- EmployeeApi.create(CreateEmployeeRequest(firstName, lastName))
      yield savedEmployee
    )

    case Read extends CRUDOperation(2, "Read existing employee", () =>
      for
        _ <- printLine("Please enter the id of the employee you want to read: ")
        id <- readLine
        employee <- EmployeeApi.get(id)
      yield employee
    )

    case Update extends CRUDOperation(3, "Update existing employee", () =>
      for
        _ <- printLine("Please enter employee id: ")
        id <- readLine
        _ <- printLine("Please enter employee new first name: ")
        firstName <- readLine
        _ <- printLine("Please enter employee new last name: ")
        lastName <- readLine
        savedEmployee <- EmployeeApi.update(UpdateEmployeeRequest(id, firstName, lastName))
      yield savedEmployee
    )

    case Delete extends CRUDOperation(4, "Delete existing employee", () =>
      for
        _ <- printLine("Please enter the id of the employee you want to delete: ")
        id <- readLine
        _ <- EmployeeApi.delete(id)
      yield ()
    )

    case GetAll extends CRUDOperation(5, "Read all existing employees", () => EmployeeApi.getAll())

