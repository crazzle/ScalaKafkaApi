import sbt._

object Dependencies {
  val resolutionRepos = Seq()

  // versions for all the used libraries
   object Versions {
    val kafka           = "0.8.2.0"
    val typesafeconfig  = "1.2.1"
  }

  object Libraries {
    val typesafeconfig        = "com.typesafe"         % "config"          % Versions.typesafeconfig
    val kafkaclient           = "org.apache.kafka"     % "kafka-clients"   % Versions.kafka
    val kafka                 = ("org.apache.kafka"     % "kafka_2.10"      % Versions.kafka).
                                exclude("javax.jms", "jms").
                                exclude("com.sun.jdmk", "jmxtools").
                                exclude("com.sun.jmx", "jmxri")
  }
}
