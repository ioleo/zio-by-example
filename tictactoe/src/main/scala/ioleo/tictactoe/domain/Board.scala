package ioleo.tictactoe.domain

object Board {

  val wins: Set[Set[Field]] = {
    val horizontalWins = Set(
        Set(1, 2, 3)
      , Set(4, 5, 6)
      , Set(7, 8, 9)
    )

    val verticalWins = Set(
        Set(1, 4, 7)
      , Set(2, 5, 8)
      , Set(3, 6, 9)
    )

    val diagonalWins = Set(
        Set(1, 5, 9)
      , Set(3, 5, 7)
    )

    (horizontalWins ++ verticalWins ++ diagonalWins)
      .map(_.map(Field.withValue))
  }

  val forks: Set[(Field, Set[Field])] = {
    val centerForks = Set(
        5 -> Set(2, 4)
      , 5 -> Set(2, 6)
      , 5 -> Set(2, 8)
      , 5 -> Set(4, 6)
      , 5 -> Set(4, 8)
      , 5 -> Set(6, 8)
    )

    val diagonalForks = Set(
        1 -> Set(3, 7)
      , 3 -> Set(1, 9)
      , 7 -> Set(1, 9)
      , 9 -> Set(3, 7)
    )

    val sideForks = Set(
        2 -> Set(1, 5)
      , 2 -> Set(3, 5)
      , 2 -> Set(1, 8)
      , 2 -> Set(3, 8)
      , 4 -> Set(1, 5)
      , 4 -> Set(1, 6)
      , 4 -> Set(5, 7)
      , 4 -> Set(6, 7)
      , 6 -> Set(3, 4)
      , 6 -> Set(3, 5)
      , 6 -> Set(4, 9)
      , 6 -> Set(5, 9)
      , 8 -> Set(2, 7)
      , 8 -> Set(2, 9)
      , 8 -> Set(5, 7)
      , 8 -> Set(5, 9)
    )

    (centerForks ++ diagonalForks ++ sideForks).map {
      case (n, state) => Field.withValue(n) -> state.map(Field.withValue)
    }
  }
}
