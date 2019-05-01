package ioleo.helloworld

import zio.test.{assert, suite, test, DefaultRunnableSpec}
import zio.test.Assertion.equalTo

object ExampleSpec extends DefaultRunnableSpec(

  suite("Example")(

      test("addition") {
        assert(2 + 2, equalTo(4))
      }

    , test("multiplication") {
        assert(2 * 2, equalTo(4))
      }
  )
)
