package ioleo.tictactoe.domain

sealed trait GameCommand

object GameCommand {

  case object Menu    extends GameCommand
  case object Invalid extends GameCommand

  final case class Put(field: Field) extends GameCommand
}
