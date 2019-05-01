package ioleo.tictactoe.logic

import ioleo.tictactoe.domain.{ GameResult, Field, Piece }
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait GameLogic {

  val gameLogic: GameLogic.Service[Any]
}

object GameLogic {

  trait Service[R] {

    def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): ZIO[R, Unit, Map[Field, Piece]]

    def gameResult(board: Map[Field, Piece]): ZIO[R, Nothing, GameResult]

    def nextTurn(currentTurn: Piece): ZIO[R, Nothing, Piece]
  }
}
