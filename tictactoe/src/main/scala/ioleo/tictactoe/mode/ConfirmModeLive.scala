package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.{ConfirmCommand, ConfirmMessage, State}
import ioleo.tictactoe.parser.ConfirmCommandParser
import ioleo.tictactoe.view.ConfirmView
import zio.UIO

trait ConfirmModeLive extends ConfirmMode {

  val confirmCommandParser: ConfirmCommandParser.Service[Any]
  val confirmView: ConfirmView.Service[Any]

  val confirmMode = new ConfirmMode.Service[Any] {

    def process(input: String, state: State.Confirm): UIO[State] =
      confirmCommandParser.parse(input) map {
        case ConfirmCommand.Yes     => state.confirmed
        case ConfirmCommand.No      => state.declined
        case ConfirmCommand.Invalid => state.copy(message = ConfirmMessage.InvalidCommand)
      }

    def render(state: State.Confirm): UIO[String] =
      for {
        header  <- confirmView.header(state.action)
        content <- confirmView.content
        footer  <- confirmView.footer(state.message)
      } yield List(header, content, footer).mkString("\n\n")
  }
}
