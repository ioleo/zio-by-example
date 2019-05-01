package ioleo.tictactoe

import ioleo.tictactoe.domain.State
import zio.{console, App, UIO, ZIO, ZEnv}

object TicTacToe extends App {

  val program = {
    def loop(state: State): ZIO[app.RunLoop, Nothing, Unit] =
      app.RunLoop.>.step(state).foldM(
          _         => UIO.unit
        , nextState => loop(nextState)
      )

    loop(State.default)
  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    for {
      env <- prepareEnvironment
      out <- program.provide(env).foldM(
          error => console.putStrLn(s"Execution failed with: $error") *> UIO.succeed(1)
        , _     => UIO.succeed(0)
      )
    } yield out

  private val prepareEnvironment =
    UIO.succeed(
      new app.ControllerLive
        with app.RunLoopLive
        with cli.TerminalLive
        with logic.GameLogicLive
        with logic.OpponentAiLive
        with mode.ConfirmModeLive
        with mode.GameModeLive
        with mode.MenuModeLive
        with parser.ConfirmCommandParserLive
        with parser.GameCommandParserLive
        with parser.MenuCommandParserLive
        with view.ConfirmViewLive
        with view.GameViewLive
        with view.MenuViewLive
        with zio.console.Console.Live
        with zio.random.Random.Live {}
    )
}
