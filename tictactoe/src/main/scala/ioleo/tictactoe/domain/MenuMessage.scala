package ioleo.tictactoe.domain

sealed trait MenuMessage

object MenuMessage {

  case object Empty          extends MenuMessage
  case object InvalidCommand extends MenuMessage
}
