package producer

import configuration.KafkaConfig
import producer.KafkaMessageProducer._
import info.batey.kafka.unit.KafkaUnit
import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class KafkaProducerSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {
  val zookeeperPort = 1024
  val brokerPort = 5001
  val kafkaUnitServer = new KafkaUnit(zookeeperPort, brokerPort)
  val bootstrapServers = s"localhost:$brokerPort"
  val (message1, message2, message3) = ("message1", "message2", "message3")

  override def beforeAll {
    kafkaUnitServer.startup()
  }

  override def afterAll {
    kafkaUnitServer.shutdown()
  }

  "A KafkaProducer" when {

    "a single message is sent to a topic" should {

      "push the message to Kafka" in {
        val topic = prepareTopic
        val producer = KafkaMessageProducer(new KafkaConfig("producer"))
        Await.result(producer.sendMessage(topic, message1), Duration.Inf)

        val messages = kafkaUnitServer.readMessages(topic, 1)
        messages.size() should be (1)
        messages.get(0) should be (message1)
      }
    }

    "multiple messages are sent to a topic" should {

      "push all messages to Kafka" in {
        val topic = prepareTopic
        val producer = KafkaMessageProducer(new KafkaConfig("producer"))
        val messages = Seq(message1, message2, message3)
        messages.foreach(msg => Await.result(producer.sendMessage(topic, msg), Duration.Inf))

        val results = kafkaUnitServer.readMessages(topic, messages.size)
        val test = results.forall(m => messages.contains(m))
        test should be (true)
      }
    }
  }

  def prepareTopic: String = {
    val topic = java.util.UUID.randomUUID.toString
    kafkaUnitServer.createTopic(topic)
    topic
  }
}
