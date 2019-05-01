package ioleo.tictactoe.app

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage, GameMessage, GameResult, MenuMessage, Piece, Player, State}
import ioleo.tictactoe.mode.{ConfirmMode, GameMode, MenuMode}
import zio.Managed
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isLeft, isRight, isUnit}
import zio.test.mock.Expectation.{nothing, value}

import ControllerSpecUtils._

object ControllerSpec extends DefaultRunnableSpec(
    suite("Controller")(
        suite("to process <user input>")(
            testM("`State.Confirm` delegates to ConfirmMode") {
              val app  = Controller.>.process(userInput, confirmState)
              val mock = ConfirmMode.process(equalTo(userInput -> confirmState)) returns value(menuState)
              val env  = makeEnv(confirmModeEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(menuState)))
            }
          , testM("`State.Game` delegates to GameMode") {
              val app  = Controller.>.process(userInput, gameState)
              val mock = GameMode.process(equalTo(userInput -> gameState)) returns value(menuState)
              val env  = makeEnv(gameModeEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(menuState)))
            }
          , testM("`State.Menu` delegates to MenuMode") {
              val app  = Controller.>.process(userInput, menuState)
              val mock = MenuMode.process(equalTo(userInput -> menuState)) returns value(confirmState)
              val env  = makeEnv(menuModeEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(confirmState)))
            }
          , testM("`State.Shutdown` fails with Unit") {
              val app = Controller.>.process(userInput, shutdownState)
              val env = makeEnv()

              val result = app.either.provideManaged(env)
              assertM(result, isLeft(isUnit))
            }
        )
      , suite("to render")(
            testM("`State.Confirm` delegates to ConfirmMode") {
              val app  = Controller.>.render(confirmState)
              val mock = ConfirmMode.render(equalTo(confirmState)) returns value(renderedFrame)
              val env  = makeEnv(confirmModeEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(renderedFrame)))
            }
          , testM("`State.Game` delegates to GameMode") {
              val app  = Controller.>.render(gameState)
              val mock = GameMode.render(equalTo(gameState)) returns value(renderedFrame)
              val env  = makeEnv(gameModeEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(renderedFrame)))
            }
          , testM("`State.Menu` delegates to MenuMode") {
              val app  = Controller.>.render(menuState)
              val mock = MenuMode.render(equalTo(menuState)) returns value(renderedFrame)
              val env  = makeEnv(menuModeEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(renderedFrame)))
            }
          , testM("`State.Shutdown` returns shutdown message") {
              val app = Controller.>.render(shutdownState)
              val env = makeEnv()

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(shutdownMessage)))
            }
        )
    )
)

object ControllerSpecUtils {

  val confirmState  = State.Confirm(ConfirmAction.NewGame, State.default, State.default, ConfirmMessage.Empty)
  val gameState     = State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)
  val menuState     = State.Menu(None, MenuMessage.Empty)
  val shutdownState = State.Shutdown

  val userInput       = "<user-input>"
  val renderedFrame   = "<rendered-frame>"
  val shutdownMessage = "Shutting down..."

  def makeEnv(
      confirmModeEnv: Managed[Nothing, ConfirmMode] = nothing
    , gameModeEnv: Managed[Nothing, GameMode] = nothing
    , menuModeEnv: Managed[Nothing, MenuMode] = nothing
  ): Managed[Nothing, ControllerLive] =
    (confirmModeEnv &&& gameModeEnv &&& menuModeEnv).map { case ((c, g), m) =>
      new ControllerLive {
        val confirmMode = c.confirmMode
        val gameMode = g.gameMode
        val menuMode = m.menuMode
      }
    }
}
