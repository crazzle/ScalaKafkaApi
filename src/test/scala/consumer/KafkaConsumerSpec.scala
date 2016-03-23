package consumer

import configuration.KafkaConfig
import consumer.KafkaMessageConsumer._
import info.batey.kafka.unit.KafkaUnit
import kafka.producer.KeyedMessage
import kafka.serializer.DefaultDecoder
import org.scalatest.{BeforeAndAfterAll, ShouldMatchers, WordSpec}

class KafkaConsumerSpec extends WordSpec with ShouldMatchers with BeforeAndAfterAll {
  val zookeeperPort = 1026
  val brokerPort = 5003
  val kafkaUnitServer = new KafkaUnit(zookeeperPort, brokerPort)
  val zookeeperConnect = s"localhost:$zookeeperPort"
  val (message1, message2, message3) = ("message1", "message2", "message3")
  val (bytes1, bytes2, bytes3) = (
    Array(109, 101, 115, 115, 97, 103, 101, 49),
    Array(109, 101, 115, 115, 97, 103, 101, 50),
    Array(109, 101, 115, 115, 97, 103, 101, 51))

  val kafkaConfig = new KafkaConfig("consumer1")

  override def beforeAll {
    kafkaUnitServer.startup()
  }

  override def afterAll {
    kafkaUnitServer.shutdown()
  }

  "A KafkaConsumer" when {

    "topics are subscribed to" should {

      "return proper stream with string message type" in {
        val topic = prepareTopic(message1, message2, message3)
        val consumer = KafkaMessageConsumer(kafkaConfig,"consumer1")

        consumer.subscribe(topic).iterator().map(_.message).take(3).toSeq should be (Seq(message1, message2, message3))
      }

      "return proper stream with other message type" in {
        val topic = prepareTopic(message1, message2, message3)
        val consumer = KafkaMessageConsumer(kafkaConfig,"consumer2")

        val messages = consumer.subscribe(new DefaultDecoder, new DefaultDecoder, topic).iterator().map(_.message).take(
          3).toSeq

        messages.head should be (bytes1)
        messages(1) should be (bytes2)
        messages(2) should be (bytes3)
      }
    }

    "topics are observed" should {

      "return proper observable with string message type" in {
        val topic = prepareTopic(message1, message2, message3)
        val consumer = KafkaMessageConsumer(kafkaConfig,"consumer3")

        var count = 0
        consumer.observe(topic).take(3).subscribe { message =>
          count = count + 1

          count match {
            case 1 => message should be (message1)
            case 2 => message should be (message2)
            case 3 => message should be (message3)
          }

        }
      }

      "return proper observable with other message type" in {
        val topic = prepareTopic(message1, message2, message3)
        val consumer = KafkaMessageConsumer(kafkaConfig,"consumer4")

        var count = 0
        consumer.observe(new DefaultDecoder, new DefaultDecoder, topic).take(3).subscribe { message =>
          count = count + 1

          count match {
            case 1 => message should be (bytes1)
            case 2 => message should be (bytes2)
            case 3 => message should be (bytes3)
          }

        }
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
