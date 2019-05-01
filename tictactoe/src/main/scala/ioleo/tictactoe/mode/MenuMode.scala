package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.State
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait MenuMode {

  val menuMode: MenuMode.Service[Any]
}

object MenuMode {

  trait Service[R] {

    def process(input: String, state: State.Menu): ZIO[R, Nothing, State]

    def render(state: State.Menu): ZIO[R, Nothing, String]
  }
}
