package ioleo.tictactoe.app

import ioleo.tictactoe.cli.Terminal
import ioleo.tictactoe.domain.State
import zio.UIO

trait RunLoopLive extends RunLoop {

  val controller: Controller.Service[Any]
  val terminal: Terminal.Service[Any]

  final val runLoop = new RunLoop.Service[Any] {

    def step(state: State) =
      for {
        frame     <- controller.render(state)
        _         <- terminal.display(frame)
        input     <- if (state == State.Shutdown) UIO.succeed("") else terminal.getUserInput
        nextState <- controller.process(input, state)
      } yield nextState
  }
}
