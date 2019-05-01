package ioleo.tictactoe.view

import ioleo.tictactoe.domain.{ConfirmAction, ConfirmMessage}
import zio.ZIO
import zio.macros.annotation.{accessible, mockable}

@accessible(">")
@mockable
trait ConfirmView {

  val confirmView: ConfirmView.Service[Any]
}

object ConfirmView {

  trait Service[R] {

    def header(action: ConfirmAction): ZIO[R, Nothing, String]

    val content: ZIO[R, Nothing, String]

    def footer(message: ConfirmMessage): ZIO[R, Nothing, String]
  }
}
