package ioleo.tictactoe.view

import ioleo.tictactoe.domain.MenuMessage
import zio.UIO
import zio.test.{assert, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}

import MenuViewSpecUtils._

object MenuViewSpec extends DefaultRunnableSpec(
    suite("MenuView")(
        suite("header")(
            testM("returns ascii art TicTacToe") {
              for {
                _      <- UIO.unit
                app    = MenuView.>.header
                env    = new MenuViewLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(asciiArtTicTacToe)))
            }
        )
      , suite("content returns list of commands")(
            testM("including `resume` if suspended") {
              for {
                _      <- UIO.unit
                app    = MenuView.>.content(true)
                env    = new MenuViewLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(suspendedCommands)))
            }
          , testM("excluding `resume` if not suspended") {
              for {
                _      <- UIO.unit
                app    = MenuView.>.content(false)
                env    = new MenuViewLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(notSuspendedCommands)))
            }
        )
      , suite("footer renders Message")(
            testM("Empty") {
              for {
                _      <- UIO.unit
                app    = MenuView.>.footer(MenuMessage.Empty)
                env    = new MenuViewLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(emptyMessage)))
            }
          , testM("InvalidCommand") {
              for {
                _      <- UIO.unit
                app    = MenuView.>.footer(MenuMessage.InvalidCommand)
                env    = new MenuViewLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(invalidCommandMessage)))
            }
        )
    )
)

object MenuViewSpecUtils {

  val asciiArtTicTacToe =
    """ _____  _        _____               _____              
      #/__   \(_)  ___ /__   \  __ _   ___ /__   \  ___    ___ 
      #  / /\/| | / __|  / /\/ / _` | / __|  / /\/ / _ \  / _ \
      # / /   | || (__  / /   | (_| || (__  / /   | (_) ||  __/
      # \/    |_| \___| \/     \__,_| \___| \/     \___/  \___|""".stripMargin('#')

  val suspendedCommands =
    """* new game
      |* resume
      |* quit""".stripMargin

  val notSuspendedCommands =
    """* new game
      |* quit""".stripMargin

  val emptyMessage =
    ""

  val invalidCommandMessage =
    "Invalid command. Try again."

}
