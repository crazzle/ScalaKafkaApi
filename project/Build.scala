import sbt.Keys._
import sbt._

object Build extends Build {

  import BuildSettings._
  import Dependencies._

  // Configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
    resourceDirectory in Compile := baseDirectory.value / "resources"
  }

  // Define our project, with basic project information and library dependencies
  lazy val project = Project("ScalaKafkaApi", file("."))
    .settings(buildSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.typesafeconfig,
        Libraries.kafka,
        Libraries.rxscala,
        Libraries.kafkaunit,
        Libraries.scalatest,
        Libraries.scalacheck
      )
    )
}