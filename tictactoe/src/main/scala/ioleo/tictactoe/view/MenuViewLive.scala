package ioleo.tictactoe.view

import ioleo.tictactoe.domain.MenuMessage
import zio.UIO

trait MenuViewLive extends MenuView {

  val menuView = new MenuView.Service[Any] {

    val header =
      UIO.succeed(
        """ _____  _        _____               _____              
          #/__   \(_)  ___ /__   \  __ _   ___ /__   \  ___    ___ 
          #  / /\/| | / __|  / /\/ / _` | / __|  / /\/ / _ \  / _ \
          # / /   | || (__  / /   | (_| || (__  / /   | (_) ||  __/
          # \/    |_| \___| \/     \__,_| \___| \/     \___/  \___|""".stripMargin('#')
      )

    def content(isSuspended: Boolean) =
      UIO.effectTotal {
        val commands =
          if (isSuspended) List("new game", "resume", "quit")
          else List("new game", "quit")

        commands
          .map(cmd => s"* $cmd")
          .mkString("\n")
      }

    def footer(message: MenuMessage) =
      UIO.succeed(message) map {
        case MenuMessage.Empty => ""
        case MenuMessage.InvalidCommand => "Invalid command. Try again."
      }
  }
}
