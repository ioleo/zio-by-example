package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.ConfirmCommand
import zio.test.{assertM, checkM, suite, testM, DefaultRunnableSpec, Gen}
import zio.test.Assertion.equalTo

import ConfirmCommandParserSpecUtils._

object ConfirmCommandParserSpec extends DefaultRunnableSpec(
    suite("ConfirmCommandParser")(
        suite("parse")(
            testM("`yes` returns Yes command") {
              checkParse("yes", ConfirmCommand.Yes)
            }
          , testM("`no` returns No command") {
              checkParse("no", ConfirmCommand.No)
            }
          , testM("any other input returns Invalid command") {
              checkM(invalidCommandsGen) { input =>
                checkParse(input, ConfirmCommand.Invalid)
              }
            }
        )
    )
)

object ConfirmCommandParserSpecUtils {

  val validCommands = List("yes", "no")

  val invalidCommandsGen =
    Gen.anyString.filter(str => !validCommands.contains(str))

  def checkParse(input: String, command: ConfirmCommand) = {
    val app    = ConfirmCommandParser.>.parse(input)
    val env    = new ConfirmCommandParserLive {}
    val result = app.provide(env)
    assertM(result, equalTo(command))
  }
}
