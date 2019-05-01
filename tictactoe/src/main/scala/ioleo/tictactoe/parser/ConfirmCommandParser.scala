package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.ConfirmCommand
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait ConfirmCommandParser {

  val confirmCommandParser: ConfirmCommandParser.Service[Any]
}

object ConfirmCommandParser {

  trait Service[R] {

    def parse(input: String): ZIO[R, Nothing, ConfirmCommand]
  }
}
