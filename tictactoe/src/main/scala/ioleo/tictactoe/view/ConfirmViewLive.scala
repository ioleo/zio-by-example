package ioleo.tictactoe.view

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage}
import zio.UIO

trait ConfirmViewLive extends ConfirmView {

  val confirmView = new ConfirmView.Service[Any] {

    def header(action: ConfirmAction): UIO[String] =
      UIO.succeed(action) map {

        case ConfirmAction.NewGame =>
          """[New game]
            |
            |This will discard current game progress.""".stripMargin
        case ConfirmAction.Quit =>

          """[Quit]
            |
            |This will discard current game progress.""".stripMargin
      }

    val content: UIO[String] =
      UIO.succeed(
        """Are you sure?
          |<yes> / <no>""".stripMargin
      )

    def footer(message: ConfirmMessage): UIO[String] =
      UIO.succeed(message) map {
        case ConfirmMessage.Empty          => ""
        case ConfirmMessage.InvalidCommand => "Invalid command. Try again."
      }
  }
}
