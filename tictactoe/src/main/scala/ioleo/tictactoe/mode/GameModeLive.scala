package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.{Field, GameCommand, GameMessage, GameResult, MenuMessage, Piece, Player, State}
import ioleo.tictactoe.logic.{GameLogic, OpponentAi}
import ioleo.tictactoe.parser.GameCommandParser
import ioleo.tictactoe.view.GameView
import zio.UIO

trait GameModeLive extends GameMode {

  val gameCommandParser: GameCommandParser.Service[Any]
  val gameLogic: GameLogic.Service[Any]
  val gameView: GameView.Service[Any]
  val opponentAi: OpponentAi.Service[Any]

  val gameMode = new GameMode.Service[Any] {

    def process(input: String, state: State.Game): UIO[State] =
      if (state.result != GameResult.Ongoing) UIO.succeed(State.Menu(None, MenuMessage.Empty))
      else if (isAiTurn(state))
        opponentAi.randomMove(state.board).orDieWith(_ => new IllegalStateException) >>= (takeField(_, state))
      else
        gameCommandParser.parse(input) >>= {
          case GameCommand.Menu       => UIO.succeed(State.Menu(Some(state), MenuMessage.Empty))
          case GameCommand.Put(field) => takeField(field, state)
          case GameCommand.Invalid    => UIO.succeed(state.copy(message = GameMessage.InvalidCommand))
        }

    private def isAiTurn(state: State.Game): Boolean =
      (state.turn == Piece.Cross && state.cross == Player.Ai) ||
      (state.turn == Piece.Nought && state.nought == Player.Ai)

    private def takeField(field: Field, state: State.Game): UIO[State] =
      (for {
        updatedBoard  <- gameLogic.putPiece(state.board, field, state.turn)
        updatedResult <- gameLogic.gameResult(updatedBoard)
        updatedTurn   <- gameLogic.nextTurn(state.turn)
      } yield state.copy(
          board  = updatedBoard
        , result = updatedResult
        , turn   = updatedTurn
      )) orElse UIO.succeed(state.copy(message = GameMessage.FieldOccupied))

    def render(state: State.Game): UIO[String] = {
      val player = if (state.turn == Piece.Cross) state.cross else state.nought
      for {
        header  <- gameView.header(state.result, state.turn, player)
        content <- gameView.content(state.board, state.result)
        footer  <- gameView.footer(state.message)
      } yield List(header, content, footer).mkString("\n\n")
    }
  }
}
