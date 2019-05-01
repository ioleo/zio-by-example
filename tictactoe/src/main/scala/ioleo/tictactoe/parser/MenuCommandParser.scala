package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.MenuCommand
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait MenuCommandParser {

  val menuCommandParser: MenuCommandParser.Service[Any]
}

object MenuCommandParser {

  trait Service[R] {

    def parse(input: String): ZIO[R, Nothing, MenuCommand]
  }
}
