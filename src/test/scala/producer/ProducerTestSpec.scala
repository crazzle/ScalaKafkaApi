package producer

import info.batey.kafka.unit.KafkaUnit
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import org.scalatest.{BeforeAndAfter, ShouldMatchers, WordSpec}
import scala.collection.JavaConversions._

/**
  * Created by markkeinhorster on 07.03.16.
  */
class ProducerTestSpec extends WordSpec with ShouldMatchers with BeforeAndAfter{

  val kafkaUnitServer = new KafkaUnit(1024, 5001)

  before {
    kafkaUnitServer.startup()
  }

  after{
    kafkaUnitServer.shutdown()
  }

  "A ProducerSpec" when {
    "executed" should {
      "push a message into a defined topic" in {
        val topic = "producerTest1"
        kafkaUnitServer.createTopic(topic);
        val producer = new KafkaMessageProducer[String](topic)
        producer.sendMessage("testMessage")
        val messages = kafkaUnitServer.readMessages(topic, 1)
        messages.size() should be(1)
        messages.get(0) should be("testMessage")
      }

      "push many messages into a defined topic" in {
        val topic = "producerTest2"
        kafkaUnitServer.createTopic(topic);
        val producer = new KafkaMessageProducer[String](topic)
        val messages = Gen.containerOf[List,String](Gen.numStr)
        val prop = forAll(messages){(msgs : List[String]) =>
          msgs.foreach(m => producer.sendMessage(m))
          val results = kafkaUnitServer.readMessages(topic, msgs.size)
          val test = results.forall(m => msgs.contains(m))
          test should be(true)
          true
        }
        prop.check
      }
    }
  }
}
