package net.xrrocha.ziocrud

import domain.model.EmployeeRepository.EmployeeRepository
import infrastructure.Controller
import infrastructure.environment.EmployeeRepositoryEnv

import zio.Console.{printLine, readLine}
import zio.{Console, ExitCode, Has, URIO, ZIO}

import java.io.IOException

object Application extends zio.App :

  type ApplicationEnvironment = Has[Console] with EmployeeRepository

  val localApplicationEnvironment = Console.live ++ EmployeeRepositoryEnv.inMemory

  def run(args: List[String]) =
    val result =
      val profile = args.headOption.getOrElse("")
      if (profile == "local") program.provideLayer(localApplicationEnvironment)
      else printLine(s"Unsupported profile $profile") *> ZIO.fail(1)

    result.exitCode

  def program: ZIO[ApplicationEnvironment, IOException, Unit] =
    for
      _ <- printLine("Please select next operation to perform:")
      _ <- Controller.menuItems.map((index, title) => printLine(s"$index: $title")).reduce(_ *> _)
      _ <- printLine("q: Exit application")
      selection <- readLine.map(_.trim.toLowerCase)
      _ <-
        if selection == "q" then
          printLine(s"Shutting down")
        else
          Controller.selectOperation(selection) match
            case Some(op) =>
              op()
                .tapError(e => printLine(s"Failed with: $e"))
                .flatMap(s => printLine(s"Succeeded with $s") *> program)
                .orElse(program)
            case None =>
              printLine(s"'$selection' is not a valid selection, please try again!") *> program
    yield ()
