package ioleo.helloworld

import zio.test.{assert, suite, testM, DefaultRunnableSpec}
import zio.test.environment.TestConsole
import zio.test.Assertion.equalTo

object HelloWorldSpec extends DefaultRunnableSpec(

  suite("HelloWorld")(

      testM("prints to console") {
        for {
          test  <- TestConsole.makeTest(TestConsole.DefaultData)
          env  = new TestConsole {
            val console = test
          }
          _    <- HelloWorld.program.provide(env)
          out  <- test.output
        } yield assert(out, equalTo(Vector("Hello world!\n")))
      }
  )
)
