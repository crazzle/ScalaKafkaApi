package producer

import configuration.{KafkaProducerConfig, KafkaConfig}
import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer}
import scala.collection.JavaConversions._
import KafkaMessageProducer.config

case class KafkaMessageProducer[A](topic: String) {
  private lazy val producer = new KafkaProducer[A,A](config asMap)

  private def record(topic: String, record: A) = new ProducerRecord[A, A](topic, record)
  private def send(producer: KafkaProducer[A, A], record: ProducerRecord[A, A]) = producer.send(record)

  def sendMessage(rec: A) = send(producer, record(topic, rec))
  def close() = producer.close()
}
object KafkaMessageProducer{
  lazy val config = new KafkaConfig() with KafkaProducerConfig
}