package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.ConfirmCommand
import zio.UIO

trait ConfirmCommandParserLive extends ConfirmCommandParser {

  val confirmCommandParser = new ConfirmCommandParser.Service[Any] {

    def parse(input: String): UIO[ConfirmCommand] =
      UIO.succeed(input) map {
        case "yes" => ConfirmCommand.Yes
        case "no"  => ConfirmCommand.No
        case _     => ConfirmCommand.Invalid
      }
  }
}
