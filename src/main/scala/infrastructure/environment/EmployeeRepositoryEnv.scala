package net.xrrocha.ziocrud.infrastructure.environment

import net.xrrocha.ziocrud.domain.model.EmployeeRepository
import net.xrrocha.ziocrud.infrastructure.persistence.EmployeeRepositoryInMemory
import zio.{Has, Layer, ZLayer}

object EmployeeRepositoryEnv:

  val inMemory: Layer[Nothing, Has[EmployeeRepository.Service]] = ZLayer.succeed(EmployeeRepositoryInMemory)
