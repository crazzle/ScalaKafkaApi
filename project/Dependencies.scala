import sbt._

object Dependencies {
  val resolutionRepos = Seq()

  // versions for all the used libraries
   object Versions {
    val kafka           = "0.8.2.2"
    val typesafeconfig  = "1.2.1"
    val kafkaunit       = "0.3"
    val scalatest       = "2.2.6"
    val scalacheck      = "1.13.0"
  }

  object Libraries {
    val typesafeconfig        = "com.typesafe"         % "config"          % Versions.typesafeconfig
    val kafka                 = ("org.apache.kafka"    %% "kafka"          % Versions.kafka).
                                exclude("javax.jms", "jms").
                                exclude("com.sun.jdmk", "jmxtools").
                                exclude("com.sun.jmx", "jmxri")
    val kafkaunit            = "info.batey.kafka"      % "kafka-unit"      % Versions.kafkaunit           % "test"
    val scalatest            = "org.scalatest"         %% "scalatest"      % Versions.scalatest           % "test"
    val scalacheck           = "org.scalacheck"        %% "scalacheck"     % Versions.scalacheck          % "test"
  }
}
