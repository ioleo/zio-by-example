package ioleo.tictactoe.view

import ioleo.tictactoe.domain.{Field, GameMessage, GameResult, Piece, Player}
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait GameView {

  val gameView: GameView.Service[Any]
}

object GameView {

  trait Service[R] {

    def header(result: GameResult, turn: Piece, player: Player): ZIO[R, Nothing, String]

    def content(board: Map[Field, Piece], result: GameResult): ZIO[R, Nothing, String]

    def footer(message: GameMessage): ZIO[R, Nothing, String]
  }
}
