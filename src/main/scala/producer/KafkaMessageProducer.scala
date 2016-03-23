package producer

import configuration.KafkaConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.concurrent.{ExecutionContext, Future, blocking}

object KafkaMessageProducer {

  def apply(config: KafkaConfig): KafkaProducer[String, String] = {
    new KafkaProducer(config asProperty)
  }

  implicit class KafkaProducerOps[KeyType, ValueType](producer: KafkaProducer[KeyType, ValueType]) {
    def sendMessage(topic: String, message: ValueType)(implicit ec: ExecutionContext): Future[RecordMetadata] = Future {
      blocking {
        sendRecord(producer, createRecord(topic, message)).get()
      }
    }

    private def sendRecord(producer: KafkaProducer[KeyType, ValueType], record: ProducerRecord[KeyType, ValueType]) = {
      producer.send(record)
    }

    private def createRecord(topic: String, record: ValueType): ProducerRecord[KeyType, ValueType] = {
      new ProducerRecord(topic, record)
    }
  }
}
