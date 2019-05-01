package ioleo.tictactoe.domain

sealed trait State

object State {

  final case class Confirm(
      action: ConfirmAction
    , confirmed: State
    , declined: State
    , message: ConfirmMessage
  ) extends State

  final case class Menu(
      game: Option[Game]
    , message: MenuMessage
  ) extends State

  final case class Game(
      board: Map[Field, Piece]
    , cross: Player
    , nought: Player
    , turn: Piece
    , result: GameResult
    , message: GameMessage
  ) extends State

  case object Shutdown extends State

  def default(): State =
    State.Menu(None, MenuMessage.Empty)
}
