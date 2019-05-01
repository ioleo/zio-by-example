package ioleo.tictactoe.view

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage}
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}

import ConfirmViewSpecUtils._

object ConfirmViewSpec extends DefaultRunnableSpec(
    suite("ConfirmView")(
        suite("header returns action description")(
            testM("NewGame") {
              val app = ConfirmView.>.header(ConfirmAction.NewGame)
              val env = new ConfirmViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(newGameDescription)))
            }
          , testM("Quit") {
              val app = ConfirmView.>.header(ConfirmAction.Quit)
              val env = new ConfirmViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(quitDescription)))
            }
        )
      , suite("content")(
            testM("returns confirm prompt") {
              val app = ConfirmView.>.content
              val env = new ConfirmViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(confirmPrompt)))
            }
        )
      , suite("footer renders Message")(
            testM("Empty") {
              val app = ConfirmView.>.footer(ConfirmMessage.Empty)
              val env = new ConfirmViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(emptyMessage)))
            }
          , testM("InvalidCommand") {
              val app = ConfirmView.>.footer(ConfirmMessage.InvalidCommand)
              val env = new ConfirmViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(invalidCommandMessage)))
            }
        )
    )
)

object ConfirmViewSpecUtils {

  val newGameDescription =
    """[New game]
      |
      |This will discard current game progress.""".stripMargin

  val quitDescription =
    """[Quit]
      |
      |This will discard current game progress.""".stripMargin

  val confirmPrompt =
    """Are you sure?
      |<yes> / <no>""".stripMargin

  val emptyMessage =
    ""

  val invalidCommandMessage =
    "Invalid command. Try again."
}
