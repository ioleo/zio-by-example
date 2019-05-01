package ioleo.tictactoe.view

import ioleo.tictactoe.domain.{Field, GameMessage, GameResult, Piece, Player}
import zio.UIO

trait GameViewLive extends GameView {

  val gameView = new GameView.Service[Any] {

    def header(result: GameResult, turn: Piece, player: Player): UIO[String] =

      UIO.succeed(result) map {
        case GameResult.Ongoing if player == Player.Human =>
          s"""$turn turn
             |
             |Select field number or type `menu` and confirm with <enter>.""".stripMargin

        case GameResult.Ongoing if player == Player.Ai =>
          s"""$turn turn
             |
             |Calculating computer opponent move. Press <enter> to continue.""".stripMargin

        case GameResult.Win(piece) =>
          s"""The game ended with $piece win.
             |
             |Press <enter> to continue.""".stripMargin

        case GameResult.Draw =>
          s"""The game ended in a draw.
             |
             |Press <enter> to continue.""".stripMargin
      }

    def content(board: Map[Field, Piece], result: GameResult): UIO[String] =
      UIO.effectTotal {
        Field
          .values
          .sortBy(_.value)
          .map(field => board.get(field) -> field.value)
          .map {
            case (Some(Piece.Cross), _)  => "x"
            case (Some(Piece.Nought), _) => "o"
            case (None, value)           => if (result == GameResult.Ongoing) value.toString else " "
          }
          .sliding(3, 3)
          .map(fields => s""" ${fields.mkString(" ║ ")} """)
          .mkString("\n═══╬═══╬═══\n")
      }

    def footer(message: GameMessage): UIO[String] =
      UIO.succeed(message) map {
        case GameMessage.Empty          => ""
        case GameMessage.InvalidCommand => "Invalid command. Try again."
        case GameMessage.FieldOccupied  => "Field occupied. Try another."
      }
  }
}
