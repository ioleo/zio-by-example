package ioleo.tictactoe.domain

sealed trait ConfirmAction

object ConfirmAction {

  case object NewGame extends ConfirmAction
  case object Quit    extends ConfirmAction
}
