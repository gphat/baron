import sbt._
import Keys._
import play.Project._
import sbtbuildinfo.Plugin._

object ApplicationBuild extends Build {

  val appName         = "baron"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "mysql"         % "mysql-connector-java"    % "5.1.23",
    "joda-time"     % "joda-time"               % "2.1"
  )

  val main = play.Project(appName, appVersion, appDependencies, settings = Defaults.defaultSettings ++ buildInfoSettings).settings(
    // Add your own project settings here
  ).settings(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "baron"
  )
}
