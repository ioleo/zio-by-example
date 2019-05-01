package ioleo.helloworld

import zio._
import zio.console.Console

object HelloWorld extends App {

  val program: ZIO[Console, Nothing, Unit] =
    console.putStrLn("Hello world!")

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program.foldM(
        error => console.putStrLn(s"Execution failed with: $error") *> ZIO.succeed(1)
      , _ => ZIO.succeed(0)
    )
}
