package consumer

import info.batey.kafka.unit.KafkaUnit
import kafka.producer.KeyedMessage
import org.scalatest.{BeforeAndAfter, ShouldMatchers, WordSpec}
import producer.KafkaMessageProducer

/**
  * Created by markkeinhorster on 07.03.16.
  */
class ConsumerSpec extends WordSpec with ShouldMatchers with BeforeAndAfter {

  val kafkaUnitServer = new KafkaUnit(5000, 9999)
  val topic = "consumerTest1"

  before {
    kafkaUnitServer.startup()
    kafkaUnitServer.createTopic(topic)
  }

  after {
    kafkaUnitServer.shutdown()
  }

  "A ConsumerSpec" when {
    "executed" should {
      "receive a message for a defined topic" in {
        kafkaUnitServer.sendMessages(new KeyedMessage[String,String](topic, "test"))
        val consumer = KafkaMessageConsumer(topic)
        consumer.readNext() should be(Some("test"))
      }

      "poll a batch of messages for a defined topic" in {
        val topic = "consumerTest2"
        kafkaUnitServer.createTopic(topic)
        kafkaUnitServer.sendMessages(new KeyedMessage[String,String](topic, "test"))
        kafkaUnitServer.sendMessages(new KeyedMessage[String,String](topic, "test2"))
        kafkaUnitServer.sendMessages(new KeyedMessage[String,String](topic, "test3"))
        val consumer = KafkaMessageConsumer(topic)
        consumer.grabInDuration() should be(Seq("test", "test2", "test3"))
      }
    }
  }
}
