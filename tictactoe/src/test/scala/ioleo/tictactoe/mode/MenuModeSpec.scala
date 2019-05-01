package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage, GameMessage, GameResult, MenuCommand, MenuMessage, Field, Piece, Player, State}
import ioleo.tictactoe.parser.MenuCommandParser
import ioleo.tictactoe.view.MenuView
import zio.Managed
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isFalse, isRight, isTrue}
import zio.test.mock.Expectation.{nothing, value}

import MenuModeSpecUtils._

object MenuModeSpec extends DefaultRunnableSpec(
    suite("MenuMode")(
        suite("process")(
            suite("game in progress")(
                testM("`new game` returns confirm state") {
                  checkProcess("new game", MenuCommand.NewGame, suspendedMenuState, confirmNewGameState)
                }
              , testM("`resume` returns current game state") {
                  checkProcess("resume", MenuCommand.Resume, suspendedMenuState, runningGameState)
                }
              , testM("`quit` returns confirm state") {
                  checkProcess("quit", MenuCommand.Quit, suspendedMenuState, confirmQuitState)
                }
            )
          , suite("no game in progress")(
                testM("`new game` returns new game state") {
                  checkProcess("new game", MenuCommand.NewGame, menuState, newGameState)
                }
              , testM("`resume` returns current state with Message.InvalidCommand") {
                  checkProcess("resume", MenuCommand.Resume, menuState, invalidCommandState)
                }
              , testM("`quit` returns shutdown state") {
                  checkProcess("quit", MenuCommand.Quit, menuState, State.Shutdown)
                }
            )
        )
      , suite("render")(
            testM("game in progress returns suspended menu frame") {
              checkRender(suspendedMenuState, (
                (MenuView.header returns value("header")) *>
                (MenuView.content(isTrue) returns value("content")) *>
                (MenuView.footer(equalTo(MenuMessage.Empty)) returns value("footer"))
              ))
            }
          , testM("no game in progress returns default menu frame") {
              checkRender(menuState, (
                (MenuView.header returns value("header")) *>
                (MenuView.content(isFalse) returns value("content")) *>
                (MenuView.footer(equalTo(MenuMessage.Empty)) returns value("footer"))
              ))
            }
        )
    )
)

object MenuModeSpecUtils {

  val newGameState     = State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)
  val runningGameState =
    State.Game(Map(
        Field.North -> Piece.Cross
      , Field.South -> Piece.Nought
    ), Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)

  val menuState           = State.Menu(None, MenuMessage.Empty)
  val suspendedMenuState  = State.Menu(Some(runningGameState), MenuMessage.Empty)
  val confirmNewGameState = State.Confirm(ConfirmAction.NewGame, newGameState, suspendedMenuState, ConfirmMessage.Empty)
  val confirmQuitState    = State.Confirm(ConfirmAction.Quit, State.Shutdown, suspendedMenuState, ConfirmMessage.Empty)
  val invalidCommandState = State.Menu(None, MenuMessage.InvalidCommand)

  val renderedFrame = List("header", "content", "footer").mkString("\n\n")

  def makeEnv(
      menuCommandParserEnv: Managed[Nothing, MenuCommandParser] = nothing
    , menuViewEnv: Managed[Nothing, MenuView] = nothing
  ): Managed[Nothing, MenuModeLive] =
    (menuCommandParserEnv &&& menuViewEnv).map { case (p, v) =>
      new MenuModeLive {
        val menuCommandParser = p.menuCommandParser
        val menuView          = v.menuView
      }
    }

  def checkProcess(input: String, command: MenuCommand, state: State.Menu, updatedState: State) = {
    val app  = MenuMode.>.process(input, state)
    val mock = MenuCommandParser.parse(equalTo(input)) returns value(command)
    val env  = makeEnv(menuCommandParserEnv = mock)

    val result = app.either.provideManaged(env)
    assertM(result, isRight(equalTo(updatedState)))
  }

  def checkRender(state: State.Menu, mock: Managed[Nothing, MenuView]) = {
    val app = MenuMode.>.render(state)
    val env = makeEnv(menuViewEnv = mock)

    val result = app.either.provideManaged(env)
    assertM(result, isRight(equalTo(renderedFrame)))
  }
}
