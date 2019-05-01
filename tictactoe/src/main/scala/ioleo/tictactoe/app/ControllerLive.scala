package ioleo.tictactoe.app

import ioleo.tictactoe.domain.State
import ioleo.tictactoe.mode.{ConfirmMode, GameMode, MenuMode}
import zio.{IO, UIO, ZIO}

trait ControllerLive extends Controller {

  val confirmMode: ConfirmMode.Service[Any]
  val gameMode: GameMode.Service[Any]
  val menuMode: MenuMode.Service[Any]

  val controller = new Controller.Service[Any] {

    def process(input: String, state: State): IO[Unit, State] =
      state match {
        case s: State.Confirm => confirmMode.process(input, s)
        case s: State.Game    => gameMode.process(input, s)
        case s: State.Menu    => menuMode.process(input, s)
        case State.Shutdown   => ZIO.fail[Unit](())
      }

    def render(state: State) =
      state match {
        case s: State.Confirm => confirmMode.render(s)
        case s: State.Game    => gameMode.render(s)
        case s: State.Menu    => menuMode.render(s)
        case State.Shutdown   => UIO.succeed("Shutting down...")
      }
  }
}
