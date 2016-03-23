package consumer

import configuration.KafkaConfig
import consumer.KafkaMessageConsumer._
import info.batey.kafka.unit.KafkaUnit
import kafka.producer.KeyedMessage
import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class KafkaStreamSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {
  val zookeeperPort = 1025
  val brokerPort = 5002
  val kafkaUnitServer = new KafkaUnit(zookeeperPort, brokerPort)
  val zookeeperConnect = s"localhost:$zookeeperPort"
  val (message1, message2, message3) = ("message1", "message2", "message3")

  val kafkaConfig = new KafkaConfig("consumer2")


  override def beforeAll {
    kafkaUnitServer.startup()
  }

  override def afterAll {
    kafkaUnitServer.shutdown()
  }

  "A KafkaStream" when {

    "poll is called" should {

      "return the first message if a single message exists for the subscribed topic" in {
        val topic = prepareTopic(message1)
        val consumer = KafkaMessageConsumer(kafkaConfig,"consumer1")
        val stream = consumer.subscribe(topic)

        Await.result(stream.poll, Duration.Inf) should be (message1)
      }

      "return the first message if multiple messages exist for the subscribed topic" in {
        val topic = prepareTopic(message1, message2, message3)
        val consumer = KafkaMessageConsumer(kafkaConfig,"consumer2")
        val stream = consumer.subscribe(topic)

        Await.result(stream.poll, Duration.Inf) should be (message1)
      }
    }
  }

  def prepareTopic(messages: String*): String = {
    val topic = java.util.UUID.randomUUID.toString
    kafkaUnitServer.createTopic(topic)
    messages.foreach(sendMessage(topic, _))
    topic
  }

  def sendMessage(topic: String, message: String): Unit = {
    kafkaUnitServer.sendMessages(new KeyedMessage(topic, message))
  }
}
