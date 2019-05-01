package ioleo.tictactoe.view

import ioleo.tictactoe.domain.{Field, GameMessage, GameResult, Piece}
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}

import GameViewSpecUtils._

object GameViewSpec extends DefaultRunnableSpec(
    suite("GameView")(
        suite("content renders")(
            testM("empty board") {
              val app = GameView.>.content(emptyBoard, GameResult.Ongoing)
              val env = new GameViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(emptyBoardView)))
            }
          , testM("non empty board") {
              val app = GameView.>.content(nonEmptyBoard, GameResult.Ongoing)
              val env = new GameViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(nonEmptyBoardView)))
            }
        )
      , suite("footer renders message")(
            testM("Empty") {
              val app = GameView.>.footer(GameMessage.Empty)
              val env = new GameViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(emptyMessage)))
            }
          , testM("InvalidCommand") {
              val app = GameView.>.footer(GameMessage.InvalidCommand)
              val env = new GameViewLive {}

              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(invalidCommandMessage)))
            }
        )
    )
)

object GameViewSpecUtils {

  val emptyBoard = Map.empty[Field, Piece]

  val emptyBoardView =
    """ 1 ║ 2 ║ 3 
      |═══╬═══╬═══
      | 4 ║ 5 ║ 6 
      |═══╬═══╬═══
      | 7 ║ 8 ║ 9 """.stripMargin

  val nonEmptyBoard = Map[Field, Piece](
      Field.NorthWest -> Piece.Cross
    , Field.West      -> Piece.Nought
    , Field.Center    -> Piece.Cross
    , Field.SouthEast -> Piece.Nought
  )

  val nonEmptyBoardView =
    """ x ║ 2 ║ 3 
      |═══╬═══╬═══
      | o ║ x ║ 6 
      |═══╬═══╬═══
      | 7 ║ 8 ║ o """.stripMargin

  val emptyMessage = ""

  val invalidCommandMessage =
    "Invalid command. Try again."
}
