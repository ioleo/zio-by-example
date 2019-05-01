package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.GameCommand
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait GameCommandParser {

  val gameCommandParser: GameCommandParser.Service[Any]
}

object GameCommandParser {

  trait Service[R] {

    def parse(input: String): ZIO[R, Nothing, GameCommand]
  }
}
