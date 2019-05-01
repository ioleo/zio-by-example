package ioleo.tictactoe.logic

import ioleo.tictactoe.domain.{Field, Piece}
import zio.IO
import zio.random.Random

trait OpponentAiLive extends OpponentAi {

  val random: Random.Service[Any]

  val opponentAi = new OpponentAi.Service[Any] {

    def randomMove(board: Map[Field, Piece]): IO[Unit, Field] = {
      val unoccupied = (Field.values.toSet -- board.keys.toSet)
      unoccupied.size match {
        case 0 => IO.fail(())
        case n => random.nextInt(n) map (idx => unoccupied.toList(idx))
      }
    }
  }
}
