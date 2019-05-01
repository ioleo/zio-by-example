package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmCommand, ConfirmMessage, GameMessage, GameResult, MenuMessage, Piece, Player, State}
import ioleo.tictactoe.parser.ConfirmCommandParser
import ioleo.tictactoe.view.ConfirmView
import zio.Managed
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}
import zio.test.mock.Expectation.{nothing, value}

import ConfirmModeSpecUtils._

object ConfirmModeSpec extends DefaultRunnableSpec(
    suite("ConfirmMode")(
        suite("process")(
            testM("`yes` returns confirmed state") {
              checkProcess("yes", ConfirmCommand.Yes, currentState, confirmedState)
            }
          , testM("`no` returns declined state") {
              checkProcess("no", ConfirmCommand.No, currentState, declinedState)
            }
          , testM("any other returns current state with Message.InvalidCommand") {
              checkProcess("foo", ConfirmCommand.Invalid, currentState, invalidCommandState)
            }
        )
      , suite("render")(
            testM("returns confirm frame") {
              val app  = ConfirmMode.>.render(currentState)
              val mock = (
                (ConfirmView.header(equalTo(ConfirmAction.NewGame)) returns value("header")) *>
                (ConfirmView.content returns value("content")) *>
                (ConfirmView.footer(equalTo(ConfirmMessage.Empty)) returns value("footer"))
              )
              val env  = makeEnv(confirmViewEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(renderedFrame)))
            }
        )
    )
)

object ConfirmModeSpecUtils {

  val confirmedState      = State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)
  val declinedState       = State.Menu(None, MenuMessage.Empty)
  val invalidCommandState = State.Confirm(ConfirmAction.NewGame, confirmedState, declinedState, ConfirmMessage.InvalidCommand)
  val currentState        = State.Confirm(ConfirmAction.NewGame, confirmedState, declinedState, ConfirmMessage.Empty)

  val renderedFrame = List("header", "content", "footer").mkString("\n\n")

  def makeEnv(
      confirmParserEnv: Managed[Nothing, ConfirmCommandParser] = nothing
    , confirmViewEnv: Managed[Nothing, ConfirmView] = nothing
  ): Managed[Nothing, ConfirmModeLive] =
    (confirmParserEnv &&& confirmViewEnv).map { case (p, c) =>
      new ConfirmModeLive {
        val confirmCommandParser = p.confirmCommandParser
        val confirmView          = c.confirmView
      }
    }

  def checkProcess(input: String, command: ConfirmCommand, state: State.Confirm, updatedState: State) = {
    val app  = ConfirmMode.>.process(input, state)
    val mock = ConfirmCommandParser.parse(equalTo(input)) returns value(command)
    val env  = makeEnv(confirmParserEnv = mock)

    val result = app.either.provideManaged(env)
    assertM(result, isRight(equalTo(updatedState)))
  }
}
