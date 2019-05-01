import sbt._
import sbt.Keys._
import mdoc.MdocPlugin.autoImport.mdocIn

object Build {

  val motd =
    """
      | Sample applications for "ZIO by example" blog post.
      |
      | Use commands:
      |
      | * `projects`               to list available applications
      | * `<projectName>/run`      to run the application
      | * `<projectName>-test/run` to run tests
      |""".stripMargin

  object v {
    // common
    val enumeratum = "1.5.13"
    val zio        = "1.0.0-RC16"
    val zioMacros  = "0.5.0"

    // tictactoe
    val atto = "0.6.5"

    // compiler plugins
    val kindProjector = "0.10.3"
    val silencer      = "1.4.0"

    // macros
    val macroParadise = "2.1.0"
  }

  val commonDeps = Seq(
      "com.beachape" %% "enumeratum"      % v.enumeratum
    , "dev.zio"      %% "zio"             % v.zio
    , "dev.zio"      %% "zio-macros-core" % v.zioMacros
    , "dev.zio"      %% "zio-macros-test" % v.zioMacros
  )

  val tictactoeDeps = Seq(
      "org.tpolecat" %% "atto-core"    % v.atto
    , "org.tpolecat" %% "atto-refined" % v.atto
  )

  val pluginDeps = Seq(
      "com.github.ghik" %% "silencer-lib" % v.silencer % Provided
  )

  val testDeps = Seq(
      "dev.zio" %% "zio-test"     % v.zio % "test"
    , "dev.zio" %% "zio-test-sbt" % v.zio % "test"
  )


  private val stdOptions = Seq(
    // see https://docs.scala-lang.org/overviews/compiler-options/index.html#Standard_Settings
      "-deprecation"
    , "-encoding", "UTF-8"
    , "-explaintypes"
    , "-feature"
    , "-language:existentials"
    , "-language:higherKinds"
    , "-language:implicitConversions"
    , "-language:postfixOps"
    , "-opt-warnings"
    , "-opt:l:inline"
    , "-opt-inline-from:<source>"
    , "-unchecked"
    , "-Yrangepos"
    , "-Ywarn-extra-implicit"
    , "-Ywarn-inaccessible"
    , "-Ywarn-numeric-widen"
    , "-Ywarn-self-implicit"
    , "-Ywarn-unused"
    , "-Ywarn-value-discard"
    , "-Xfatal-warnings"
    , "-Xlint"
    , "-Xsource:2.12"
  )

  private val libOptions = Seq(
      "-P:silencer:checkUnused"
  )

  def stdSettings(projectName: String) =
    Seq(
        name := s"zio-by-example-$projectName"
      , scalacOptions := stdOptions ++ libOptions
      , scalaVersion in ThisBuild := "2.12.9"
      , connectInput in run := true
      , fork := true
      , logBuffered := false
      , outputStrategy := Some(StdoutOutput)
      , libraryDependencies ++= (commonDeps ++ testDeps ++ pluginDeps)
      , addCompilerPlugin("org.typelevel"   %% "kind-projector"  % v.kindProjector)
      , addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % v.silencer)
      , addCompilerPlugin("org.scalamacros" % "paradise" % v.macroParadise cross CrossVersion.full)
      , testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
    )

  def mdocSettings =
    Seq(
        skip in publish := true
      , mdocIn := file(".")
      , scalacOptions -= "-Yno-imports"
      , scalacOptions -= "-Xfatal-warnings"
      , scalacOptions ~= { _ filterNot (_ startsWith "-Ywarn") }
      , scalacOptions ~= { _ filterNot (_ startsWith "-Xlint") }
      , libraryDependencies ++= Seq(
            "dev.zio" %% "zio-test"     % v.zio
          , "dev.zio" %% "zio-test-sbt" % v.zio
        )
    )
}
