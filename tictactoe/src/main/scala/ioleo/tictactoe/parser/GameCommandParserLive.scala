package ioleo.tictactoe.parser

import atto.Atto._
import atto.Parser
import ioleo.tictactoe.domain.{Field, GameCommand}
import zio.{IO, UIO}

trait GameCommandParserLive extends GameCommandParser {

  val gameCommandParser = new GameCommandParser.Service[Any] {

    def parse(input: String): UIO[GameCommand] =
      IO
        .effect(command.parse(input).done.option.get)
        .catchAll(_ => UIO.succeed(GameCommand.Invalid))

    private lazy val command: Parser[GameCommand] =
      choice(menu, put)

    private lazy val menu: Parser[GameCommand] =
      (string("menu") <~ endOfInput) >| GameCommand.Menu

    private lazy val put: Parser[GameCommand] =
      (int <~ endOfInput)
        .map { n =>
          val field = Field.withValue(n)
          GameCommand.Put(field)
        }
  }
}
