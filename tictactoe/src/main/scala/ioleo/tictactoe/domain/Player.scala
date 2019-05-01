package ioleo.tictactoe.domain

sealed trait Player

object Player {

  case object Human extends Player
  case object Ai    extends Player
}
