package ioleo.tictactoe

import zio.ZIO

final case object DummyCallError extends Exception(
    "Unexpected call to dummy service."
)


final case class AssertionFailure(message: String)
    extends Throwable(if (message.isEmpty) "assertion failed" else s"assertion failed: $message", null, true, false)

object testImplicits {

  // TODO: remove when #1610 is merged and released
  implicit class ZioOps[R, E, A](self: ZIO[R, E, A]) {

    /**
     * A less powerful variant of `assert` where the success value produced by this
     * effect is not needed.
     */
    final def assert_(p: Boolean, message: => String = ""): ZIO[R, E, A] =
      self.assert(_ => p, message)

    /**
     * Dies with a [[zio.AssertionFailure]] having the specified text message and disabled
     * ZIO tracing if assertion on success value fails, otherwise proceeds with the underlaying effect.
     */
    final def assert(p: A => Boolean, message: => String = ""): ZIO[R, E, A] =
      self.filterOrDie(p)(AssertionFailure(message))

    /**
     * Dies with specified `Throwable` if the predicate fails.
     */
    final def filterOrDie(p: A => Boolean)(t: => Throwable): ZIO[R, E, A] =
      self.filterOrElse_(p)(ZIO.die(t))

    /**
     * Dies with a [[java.lang.RuntimeException]] having the specified text message
     * if the predicate fails.
     */
    final def filterOrDieMessage(p: A => Boolean)(message: => String): ZIO[R, E, A] =
      self.filterOrElse_(p)(ZIO.dieMessage(message))
  }
}
