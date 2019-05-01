package ioleo.tictactoe.app

import ioleo.tictactoe.cli.Terminal
import ioleo.tictactoe.domain.{GameMessage, GameResult, MenuMessage, Piece, Player, State}
import zio.Managed
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}
import zio.test.mock.Expectation.{unit, value}

import RunLoopSpecUtils._

object RunLoopSpec extends DefaultRunnableSpec(
    suite("RunLoop")(
        suite("step")(
            testM("displays current state and transforms it based on user input") {
              val app = RunLoop.>.step(currentState)
              val controllerMock = (
                (Controller.render(equalTo(currentState)) returns value(renderedFrame)) *>
                (Controller.process(equalTo(userInput -> currentState)) returns value(nextState))
              )
              val terminalMock = (
                (Terminal.display(equalTo(renderedFrame)) returns unit) *>
                (Terminal.getUserInput returns value(userInput))
              )

              val env    = makeEnv(controllerMock, terminalMock)
              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(nextState)))
            }
        )
    )
)

object RunLoopSpecUtils {

  val currentState: State = State.Menu(None, MenuMessage.Empty)
  val nextState: State    = State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)

  val userInput     = "<user-input>"
  val renderedFrame = "<rendered-frame>"

  def makeEnv(
      controllerEnv: Managed[Nothing, Controller]
    , terminalEnv: Managed[Nothing, Terminal]
  ): Managed[Nothing, RunLoopLive] =
    (controllerEnv &&& terminalEnv).map { case (c, t) =>
      new RunLoopLive {
        val controller = c.controller
        val terminal   = t.terminal
      }
    }
}
