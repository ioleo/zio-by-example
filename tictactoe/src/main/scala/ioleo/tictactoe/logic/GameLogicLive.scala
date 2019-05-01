package ioleo.tictactoe.logic

import ioleo.tictactoe.domain.{ Board, GameResult, Field, Piece }
import zio.{IO, UIO, ZIO}

trait GameLogicLive extends GameLogic {

  val gameLogic = new GameLogic.Service[Any] {

    def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[Unit, Map[Field, Piece]] =
      board.get(field) match {
        case None => IO.succeed(board.updated(field, piece))
        case _    => IO.fail(())
      }

    def gameResult(board: Map[Field, Piece]): UIO[GameResult] = {
      val pieces: Map[Piece, Set[Field]] =
        board
          .groupBy(_._2)
          .mapValues(_.keys.toSet)
          .withDefaultValue(Set.empty[Field])

      val crossWin: Boolean  = Board.wins.exists(_ subsetOf pieces(Piece.Cross))
      val noughtWin: Boolean = Board.wins.exists(_ subsetOf pieces(Piece.Nought))
      val boardFull: Boolean = board.size == 9

      if (crossWin && noughtWin) ZIO.die(new IllegalStateException("It should not be possible for both players to meet winning conditions."))
      else if (crossWin) UIO.succeed(GameResult.Win(Piece.Cross))
      else if (noughtWin) UIO.succeed(GameResult.Win(Piece.Nought))
      else if (boardFull) UIO.succeed(GameResult.Draw)
      else UIO.succeed(GameResult.Ongoing)
    }

    def nextTurn(current: Piece): UIO[Piece] = UIO.succeed(current) map {
      case Piece.Cross  => Piece.Nought
      case Piece.Nought => Piece.Cross
    }
  }
}
