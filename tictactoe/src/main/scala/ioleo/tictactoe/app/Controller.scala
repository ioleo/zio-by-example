package ioleo.tictactoe.app

import ioleo.tictactoe.domain.State
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait Controller {

  val controller: Controller.Service[Any]
}

object Controller {

  trait Service[R] {

    def process(input: String, state: State): ZIO[R, Unit, State]

    def render(state: State): ZIO[R, Nothing, String]
  }
}
