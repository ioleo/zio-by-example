package ioleo.tictactoe.domain

import enumeratum.values._

sealed abstract class Field(
    val value: Int
  , val name: String
) extends IntEnumEntry

object Field extends IntEnum[Field] {

  val values = findValues

  case object NorthWest extends Field(1, "nw")
  case object North     extends Field(2, "n")
  case object NorthEast extends Field(3, "ne")
  case object West      extends Field(4, "w")
  case object Center    extends Field(5, "c")
  case object East      extends Field(6, "e")
  case object SouthWest extends Field(7, "sw")
  case object South     extends Field(8, "s")
  case object SouthEast extends Field(9, "se")
}
