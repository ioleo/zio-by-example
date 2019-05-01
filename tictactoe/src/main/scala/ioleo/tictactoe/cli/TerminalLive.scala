package ioleo.tictactoe.cli

import zio.console.Console

trait TerminalLive extends Terminal {

  val console: Console.Service[Any]

  final val terminal = new Terminal.Service[Any] {

    lazy val getUserInput =
      console.getStrLn.orDie

    def display(frame: String) =
      for {
        _ <- console.putStr(TerminalLive.ANSI_CLEARSCREEN)
        _ <- console.putStrLn(frame)
      } yield ()
  }
}

object TerminalLive {

  val ANSI_CLEARSCREEN: String =
    "\u001b[H\u001b[2J"
}
