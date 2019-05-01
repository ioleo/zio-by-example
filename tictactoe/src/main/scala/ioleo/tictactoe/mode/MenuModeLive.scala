package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage, GameMessage, GameResult, MenuCommand, MenuMessage, Piece, Player, State}
import ioleo.tictactoe.parser.MenuCommandParser
import ioleo.tictactoe.view.MenuView
import zio.UIO

trait MenuModeLive extends MenuMode {

  val menuCommandParser: MenuCommandParser.Service[Any]
  val menuView: MenuView.Service[Any]

  val menuMode = new MenuMode.Service[Any] {

    def process(input: String, state: State.Menu): UIO[State] =
      menuCommandParser.parse(input) map {
         case MenuCommand.NewGame =>
          val newGameState = State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)
          state.game match {
            case Some(_) => State.Confirm(ConfirmAction.NewGame, newGameState, state, ConfirmMessage.Empty)
            case None    => newGameState
          }
        case MenuCommand.Resume =>
          state.game match {
            case Some(gameState) => gameState
            case None            => state.copy(message = MenuMessage.InvalidCommand)
          }
        case MenuCommand.Quit =>
          state.game match {
            case Some(_) => State.Confirm(ConfirmAction.Quit, State.Shutdown, state, ConfirmMessage.Empty)
            case None    => State.Shutdown
          }
        case MenuCommand.Invalid => state.copy(message = MenuMessage.InvalidCommand)
      }

    def render(state: State.Menu): UIO[String] =
      for {
        header  <- menuView.header
        content <- menuView.content(state.game.nonEmpty)
        footer  <- menuView.footer(state.message)
      } yield List(header, content, footer).mkString("\n\n")
  }
}
