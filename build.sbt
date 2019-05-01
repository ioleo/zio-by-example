import sbt.Resolver

import scala.util.Try
import Build._

organization in ThisBuild := "ioleo"
version in ThisBuild := "0.1.0-SNAPSHOT"

lazy val `zio-by-example` =
  project
    .in(file("."))
    .aggregate(blog, helloworld, tictactoe)

lazy val blog =
  project
    .in(file("blog"))
    .settings(stdSettings("blog"))
    .settings(mdocSettings)
    .enablePlugins(MdocPlugin)

lazy val helloworld =
  project
    .in(file("helloworld"))
    .settings(stdSettings("helloworld"))

lazy val tictactoe =
  project
    .in(file("tictactoe"))
    .settings(stdSettings("tictactoe"))
    .settings(libraryDependencies ++= tictactoeDeps)

onLoad in Global := (onLoad in Global).value andThen { state: State =>
  val escapedMotd =
    motd
      .replaceAll("(\r\n)|\r|\n", "\\\\r\\\\n")
      .replaceAll("\"", "\\\\\"")

  val showMotd = s"""eval println("$escapedMotd")"""
  showMotd :: state
}
