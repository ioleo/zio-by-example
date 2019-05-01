package ioleo.tictactoe.domain

sealed trait GameResult

object GameResult {

  case object Ongoing          extends GameResult
  case class Win(piece: Piece) extends GameResult
  case object Draw             extends GameResult
}
