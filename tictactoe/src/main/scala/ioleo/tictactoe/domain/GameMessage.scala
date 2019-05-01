package ioleo.tictactoe.domain

sealed trait GameMessage

object GameMessage {

  case object Empty          extends GameMessage
  case object InvalidCommand extends GameMessage
  case object FieldOccupied  extends GameMessage
}
