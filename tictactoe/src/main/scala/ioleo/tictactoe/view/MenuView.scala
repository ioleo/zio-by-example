package ioleo.tictactoe.view

import ioleo.tictactoe.domain.MenuMessage
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait MenuView {

  val menuView: MenuView.Service[Any]
}

object MenuView {

  trait Service[R] {

    val header: ZIO[R, Nothing, String]

    def content(isSuspended: Boolean): ZIO[R, Nothing, String]

    def footer(message: MenuMessage): ZIO[R, Nothing, String]
  }
}
