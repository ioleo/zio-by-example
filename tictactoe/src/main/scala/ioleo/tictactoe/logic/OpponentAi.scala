package ioleo.tictactoe.logic

import ioleo.tictactoe.domain.{Field, Piece}
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait OpponentAi {

  val opponentAi: OpponentAi.Service[Any]
}

object OpponentAi {

  trait Service[R] {

    def randomMove(board: Map[Field, Piece]): ZIO[R, Unit, Field]
  }
}
