import sbt.Keys._
import sbt._

object BuildSettings {

  lazy val basicSettings = Seq[Setting[_]](
    organization  := "com.plainpixels.work",
    version       := "0.1.0-SNAPSHOT",
    description   := "scala kafka api wrapper",
    scalaVersion  := "2.10.6",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    resolvers     ++= Dependencies.resolutionRepos
  )

  // sbt-assembly settings for building onefat jar
  import sbtassembly.AssemblyKeys._
  import sbtassembly.AssemblyPlugin._

  lazy val sbtAssemblySettings = assemblySettings ++ Seq(
    assemblyJarName in assembly := {
      name.value + ".jar"
    }
  )

  lazy val buildSettings = basicSettings ++ sbtAssemblySettings
}
