package ioleo.tictactoe.domain

sealed trait Piece

object Piece {
  case object Nought extends Piece
  case object Cross  extends Piece
}
