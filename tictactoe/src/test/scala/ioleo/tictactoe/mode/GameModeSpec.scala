package ioleo.tictactoe.mode

import ioleo.tictactoe.domain.{GameCommand, Field, GameMessage, GameResult, MenuMessage, Piece, Player, State}
import ioleo.tictactoe.logic.{GameLogic, OpponentAi}
import ioleo.tictactoe.parser.GameCommandParser
import ioleo.tictactoe.view.GameView
import zio.Managed
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}
import zio.test.mock.Expectation.{failure, nothing, value}

import GameModeSpecUtils._

object GameModeSpec extends DefaultRunnableSpec(
    suite("GameMode")(
        suite("process")(
            testM("`menu` returns suspended menu state") {
              val app  = GameMode.>.process("menu", gameState)
              val mock = GameCommandParser.parse(equalTo("menu")) returns value(GameCommand.Menu)
              val env  = makeEnv(gameCommandParserEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(suspendedMenuState)))
            }
          , suite("`put <field>`")(
                testM("returns current state with GameMessage.FieldOccupied if field is occupied") {
                  val app        = GameMode.>.process("put 2", gameState)
                  val parserMock = GameCommandParser.parse(equalTo("put 2")) returns value(GameCommand.Put(Field.North))
                  val logicMock  = GameLogic.putPiece(equalTo((gameState.board, Field.North, Piece.Cross))) returns failure(())
                  val env        = makeEnv(gameCommandParserEnv = parserMock, gameLogicEnv = logicMock)

                  val result = app.either.provideManaged(env)
                  assertM(result, isRight(equalTo(fieldOccupiedState)))
                }
              , testM("returns state with added piece and turn advanced to next player if field is unoccupied") {
                  val app        = GameMode.>.process("put 6", gameState)
                  val parserMock = GameCommandParser.parse(equalTo("put 6")) returns value(GameCommand.Put(Field.East))
                  val logicMock  = (
                    (GameLogic.putPiece(equalTo((gameState.board, Field.East, Piece.Cross))) returns value(pieceAddedEastState.board)) *>
                    (GameLogic.gameResult(equalTo(pieceAddedEastState.board)) returns value(GameResult.Ongoing)) *>
                    (GameLogic.nextTurn(equalTo(Piece.Cross)) returns value(Piece.Nought))
                  )
                  val env        = makeEnv(gameCommandParserEnv = parserMock, gameLogicEnv = logicMock)

                  val result = app.either.provideManaged(env)
                  assertM(result, isRight(equalTo(pieceAddedEastState)))
                }
            )
          , testM("otherwise returns current state with GameMessage.InvalidCommand") {
              val app = GameMode.>.process("foo", gameState)
              val mock = GameCommandParser.parse(equalTo("foo")) returns value(GameCommand.Invalid)
              val env = makeEnv(gameCommandParserEnv = mock)

              val result = app.either.provideManaged(env)
              assertM(result, isRight(equalTo(invalidCommandState)))
            }
        )
    )
)

object GameModeSpecUtils {

  val gameState =
    State.Game(Map(
        Field.North -> Piece.Cross
      , Field.South -> Piece.Nought
    ), Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameMessage.Empty)

  val suspendedMenuState = State.Menu(Some(gameState), MenuMessage.Empty)

  val fieldOccupiedState =
    gameState.copy(message = GameMessage.FieldOccupied)

  val pieceAddedEastState =
    State.Game(Map(
        Field.North -> Piece.Cross
      , Field.South -> Piece.Nought
      , Field.East  -> Piece.Cross
    ), Player.Human, Player.Ai, Piece.Nought, GameResult.Ongoing, GameMessage.Empty)

  val invalidCommandState =
    gameState.copy(message = GameMessage.InvalidCommand)

  def makeEnv(
      gameViewEnv: Managed[Nothing, GameView] = nothing
    , gameCommandParserEnv: Managed[Nothing, GameCommandParser] = nothing
    , gameLogicEnv: Managed[Nothing, GameLogic] = nothing
    , opponentAiEnv: Managed[Nothing, OpponentAi] = nothing
  ): Managed[Nothing, GameModeLive] =
    (gameViewEnv &&& gameCommandParserEnv &&& gameLogicEnv &&& opponentAiEnv).map { case (((v, p), l), o) =>
      new GameModeLive {
        val gameView          = v.gameView
        val gameCommandParser = p.gameCommandParser
        val gameLogic         = l.gameLogic
        val opponentAi        = o.opponentAi
      }
    }
}
