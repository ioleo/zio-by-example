package ioleo.tictactoe.domain

sealed trait ConfirmCommand

object ConfirmCommand {
  case object Yes     extends ConfirmCommand
  case object No      extends ConfirmCommand
  case object Invalid extends ConfirmCommand
}
