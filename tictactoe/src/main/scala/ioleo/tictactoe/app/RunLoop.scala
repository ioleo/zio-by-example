package ioleo.tictactoe.app

import ioleo.tictactoe.domain.State
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait RunLoop {

  val runLoop: RunLoop.Service[Any]
}

object RunLoop {

  trait Service[R] {

    def step(state: State): ZIO[R, Unit, State]
  }
}
