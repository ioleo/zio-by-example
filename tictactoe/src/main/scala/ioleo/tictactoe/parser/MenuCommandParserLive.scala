package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.MenuCommand
import zio.UIO

trait MenuCommandParserLive extends MenuCommandParser {

  val menuCommandParser = new MenuCommandParser.Service[Any] {

    def parse(input: String): UIO[MenuCommand] =
      UIO.succeed(input) map {
        case "new game" => MenuCommand.NewGame
        case "resume"   => MenuCommand.Resume
        case "quit"     => MenuCommand.Quit
        case _          => MenuCommand.Invalid
      }
  }
}
