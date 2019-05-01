package ioleo.tictactoe.domain

sealed trait MenuCommand

object MenuCommand {
  case object NewGame extends MenuCommand
  case object Resume  extends MenuCommand
  case object Quit    extends MenuCommand
  case object Invalid extends MenuCommand
}
