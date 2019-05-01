package ioleo.tictactoe.parser

import ioleo.tictactoe.domain.{Field, GameCommand}
import zio.UIO
import zio.test.{assert, suite, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isRight}

object GameCommandParserSpec extends DefaultRunnableSpec(
    suite("GameCommandParser")(
        suite("parse")(
            testM("`menu` returns Menu command") {
              for {
                input  <- UIO.succeed("menu")
                app    = GameCommandParser.>.parse(input)
                env    = new GameCommandParserLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(GameCommand.Menu)))
            }
          , suite("number in range 1-9 returns Put command")(
              (1 to 9).toList.map { n =>
                testM(n.toString) {
                  for {
                    input  <- UIO.succeed(s"$n")
                    app    = GameCommandParser.>.parse(input)
                    env    = new GameCommandParserLive {}
                    output <- app.either.provide(env)
                  } yield assert(output, isRight(equalTo(GameCommand.Put(Field.withValue(n)))))
                }
              }:_*
            )
          , testM("`0` returns Invalid command") {
              for {
                input  <- UIO.succeed("put 0")
                app    = GameCommandParser.>.parse(input)
                env    = new GameCommandParserLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(GameCommand.Invalid)))
            }
          , testM("<any> returns Invalid command") {
              for {
                input  <- UIO.succeed("<any>")
                app    = GameCommandParser.>.parse(input)
                env    = new GameCommandParserLive {}
                output <- app.either.provide(env)
              } yield assert(output, isRight(equalTo(GameCommand.Invalid)))
            }
        )
    )
)
