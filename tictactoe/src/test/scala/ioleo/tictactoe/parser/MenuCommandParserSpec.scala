package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.MenuCommand
import zio.test.{assertM, checkM, suite, testM, DefaultRunnableSpec, Gen}
import zio.test.Assertion.equalTo

import MenuCommandParserSpecUtils._

object MenuCommandParserSpec extends DefaultRunnableSpec(
    suite("MenuCommandParser")(
        suite("parse")(
            testM("`new game` returns NewGame command") {
              checkParse("new game", MenuCommand.NewGame)
            }
          , testM("`resume` returns Resume command") {
              checkParse("resume", MenuCommand.Resume)
            }
          , testM("`quit` returns Quit command") {
              checkParse("quit", MenuCommand.Quit)
            }
          , testM("any other input returns Invalid command") {
              checkM(invalidCommandsGen) { input =>
                checkParse(input, MenuCommand.Invalid)
              }
            }
        )
    )
)

object MenuCommandParserSpecUtils {

  val validCommands = List("new game", "resume", "quit")

  val invalidCommandsGen =
    Gen.anyString.filter(str => !validCommands.contains(str))

  def checkParse(input: String, command: MenuCommand) = {
    val app    = MenuCommandParser.>.parse(input)
    val env    = new MenuCommandParserLive {}
    val result = app.provide(env)
    assertM(result, equalTo(command))
  }
}
