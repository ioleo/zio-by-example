package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.State
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait GameMode {

  val gameMode: GameMode.Service[Any]
}

object GameMode {

  trait Service[R] {

    def process(input: String, state: State.Game): ZIO[R, Nothing, State]

    def render(state: State.Game): ZIO[R, Nothing, String]
  }
}
