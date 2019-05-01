package ioleo.tictactoe

import ioleo.tictactoe.app.RunLoop
import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage, MenuMessage, State}
import zio.clock.Clock
import zio.duration._
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight, isSome, isUnit}
import zio.test.mock.Expectation.{failure, value}

import TicTacToeSpecUtils._

object TicTacToeSpec extends DefaultRunnableSpec(
    suite("TicTacToe")(
        suite("program")(
            testM("repeats RunLoop.step until interrupted by Unit error") {
              val app = TicTacToe.program
              val env = (
                (RunLoop.step(equalTo(state0)) returns value(state1)) *>
                (RunLoop.step(equalTo(state1)) returns value(state2)) *>
                (RunLoop.step(equalTo(state2)) returns value(state3)) *>
                (RunLoop.step(equalTo(state3)) returns failure(()))
              )

              val result = app.either.provideManaged(env).timeout(500.millis).provide(Clock.Live)
              assertM(result, isSome(isRight(isUnit)))
            }
        )
    )
)

object TicTacToeSpecUtils {

  val state0 = State.default
  val state1 = State.Menu(None, MenuMessage.InvalidCommand)
  val state2 = State.Confirm(ConfirmAction.Quit, state0, state1, ConfirmMessage.Empty)
  val state3 = State.Confirm(ConfirmAction.Quit, state0, state1, ConfirmMessage.InvalidCommand)
}
