package ioleo.tictactoe.domain

sealed trait ConfirmMessage

object ConfirmMessage {

  case object Empty          extends ConfirmMessage
  case object InvalidCommand extends ConfirmMessage
}
