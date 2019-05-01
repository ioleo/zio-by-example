package ioleo.tictactoe.logic

import ioleo.tictactoe.domain.{GameResult, Field, Piece}
import zio.test.{assertM, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{anything, equalTo, isLeft, isRight, isSubtype, isUnit}

import GameLogicSpecUtils._

object GameLogicSpec extends DefaultRunnableSpec(
    suite("GameLogic")(
        suite("putPiece")(
            testM("returns updated board if field is unoccupied") {
              val app = GameLogic.>.putPiece(board, Field.East, Piece.Cross)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(updatedBoard)))
            }
          , testM("fails if field is occupied") {
              val app = GameLogic.>.putPiece(board, Field.South, Piece.Cross)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isLeft(isUnit))
            }
        )
      , suite("gameResult")(
            testM("returns GameResult.Win(Piece.Cross) if cross won") {
              val app = GameLogic.>.gameResult(crossWinBoard)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(GameResult.Win(Piece.Cross))))
            }
          , testM("returns GameResult.Win(Piece.Nought) if nought won") {
              val app = GameLogic.>.gameResult(noughtWinBoard)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(GameResult.Win(Piece.Nought))))
            }
          , testM("returns GameResult.Draw if the board is full and there are no winners") {
              val app = GameLogic.>.gameResult(drawBoard)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(GameResult.Draw)))
            }
          , testM("returns GameResult.Ongoing if the board is not full and there are no winners") {
              val app = GameLogic.>.gameResult(ongoingBoard)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(GameResult.Ongoing)))
            }
          , testM("returns GameResult.Ongoing if the board is empty") {
              val app = GameLogic.>.gameResult(emptyBoard)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(GameResult.Ongoing)))
            }
          , testM("dies with IllegalStateException if both players are in winning position") {
              val app = GameLogic.>.gameResult(bothWinBoard)
              val env = new GameLogicLive {}
              val result = app.absorb.either.provide(env)
              assertM(result, isLeft(isSubtype[IllegalStateException](anything)))

            }
          , suite("returns GameResult.Win for all possible 3-field straight lines")(
              winningStates.map { fields =>
                val name = fields.map(_.value).mkString(", ")
                testM(name) {
                  val board = fields.map(_ -> Piece.Cross).toMap[Field, Piece]
                  val app = GameLogic.>.gameResult(board)
                  val env = new GameLogicLive {}
                  val result = app.either.provide(env)
                  assertM(result, isRight(equalTo(GameResult.Win(Piece.Cross))))
                }
              }:_*
            )
          , testM("returns GameResult.Win(Piece.Cross) for example game") {
              val app = GameLogic.>.gameResult(exampleGameBoard)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(GameResult.Win(Piece.Cross))))
            }
        )
      , suite("nextTurn")(
            testM("returns Piece.Nought given Piece.Cross") {
              val app = GameLogic.>.nextTurn(Piece.Cross)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(Piece.Nought)))
            }
          , testM("returns Piece.Cross given Piece.Nought") {
              val app = GameLogic.>.nextTurn(Piece.Nought)
              val env = new GameLogicLive {}
              val result = app.either.provide(env)
              assertM(result, isRight(equalTo(Piece.Cross)))
            }
        )
    )
)

object GameLogicSpecUtils {

  val board = Map[Field, Piece](
      Field.North -> Piece.Cross
    , Field.South -> Piece.Nought
  )

  val updatedBoard = Map[Field, Piece](
      Field.North -> Piece.Cross
    , Field.South -> Piece.Nought
    , Field.East  -> Piece.Cross
  )

  val crossWinBoard = Map[Field, Piece](
      Field.North     -> Piece.Cross
    , Field.South     -> Piece.Nought
    , Field.NorthWest -> Piece.Cross
    , Field.SouthWest -> Piece.Nought
    , Field.NorthEast -> Piece.Cross
  )

  val noughtWinBoard = Map[Field, Piece](
      Field.North     -> Piece.Nought
    , Field.South     -> Piece.Cross
    , Field.NorthWest -> Piece.Nought
    , Field.SouthWest -> Piece.Cross
    , Field.NorthEast -> Piece.Nought
  )

  val bothWinBoard = Map[Field, Piece](
      Field.North     -> Piece.Cross
    , Field.South     -> Piece.Nought
    , Field.NorthWest -> Piece.Cross
    , Field.SouthWest -> Piece.Nought
    , Field.NorthEast -> Piece.Cross
    , Field.SouthEast -> Piece.Nought
  )

  val drawBoard = Map[Field, Piece](
      Field.NorthWest -> Piece.Cross
    , Field.North     -> Piece.Cross
    , Field.NorthEast -> Piece.Nought
    , Field.West      -> Piece.Nought
    , Field.Center    -> Piece.Nought
    , Field.East      -> Piece.Cross
    , Field.SouthWest -> Piece.Cross
    , Field.South     -> Piece.Nought
    , Field.SouthEast -> Piece.Cross
  )

  val ongoingBoard = Map[Field, Piece](
      Field.North -> Piece.Nought
    , Field.South -> Piece.Cross
  )

  val exampleGameBoard = Map[Field, Piece](
      Field.NorthWest -> Piece.Cross
    , Field.North     -> Piece.Nought
    , Field.NorthEast -> Piece.Cross
    , Field.East      -> Piece.Nought
    , Field.Center    -> Piece.Cross
    , Field.West      -> Piece.Nought
    , Field.SouthWest -> Piece.Cross
  )

  val emptyBoard = Map.empty[Field, Piece]

  val winningStates: List[List[Field]] = {
    val horizontalWins = List(
        List(1, 2, 3)
      , List(4, 5, 6)
      , List(7, 8, 9)
    )

    val verticalWins = List(
        List(1, 4, 7)
      , List(2, 5, 8)
      , List(3, 6, 9)
    )

    val diagonalWins = List(
        List(1, 5, 9)
      , List(3, 5, 7)
    )

    (horizontalWins ++ verticalWins ++ diagonalWins)
      .map(_.map(Field.withValue))
  }
}
