package ioleo.tictactoe.cli

import zio.Managed
import zio.test.{assertM, checkM, suite, testM, DefaultRunnableSpec, Gen}
import zio.test.Assertion.equalTo
import zio.test.mock.Expectation.value
import zio.test.mock.MockConsole

import TerminalSpecUtils._

object TerminalSpec extends DefaultRunnableSpec(
    suite("Terminal")(
        suite("getUserInput")(
            testM("delegates to Console") {
              checkM(Gen.anyString) { input =>
                val app  = Terminal.>.getUserInput
                val mock = MockConsole.getStrLn returns value(input)
                val env  = makeEnv(mock)

                val result = app.provideManaged(env)
                assertM(result, equalTo(input))
              }
            }
        )
    )
)

object TerminalSpecUtils {

  def makeEnv(consoleEnv: Managed[Nothing, MockConsole]): Managed[Nothing, TerminalLive] =
    consoleEnv.map(c => new TerminalLive {
      val console = c.console
    })
}
