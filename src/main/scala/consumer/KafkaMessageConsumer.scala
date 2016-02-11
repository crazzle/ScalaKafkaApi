package consumer

import configuration.{KafkaConsumerConfig, KafkaConfig}
import kafka.consumer.{ConsumerConfig, Consumer, Whitelist}
import kafka.serializer.{StringDecoder}
import KafkaMessageConsumer._

case class KafkaMessageConsumer(topics : String*) {
  private val topicFilter = new Whitelist(topics.mkString(","))
  lazy val consumer = Consumer.create(new ConsumerConfig(config asProperty))
  lazy val stream = consumer.createMessageStreamsByFilter(topicFilter, numStreams, stringDecoder, stringDecoder).head
  def read() : Stream[String] = Stream.cons(stream.head.message(), read())
}
object KafkaMessageConsumer{
  lazy val config = new KafkaConfig() with KafkaConsumerConfig
  val stringDecoder = new StringDecoder()
  val numStreams = 1
}
