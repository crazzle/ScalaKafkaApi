package consumer

import java.util.concurrent.TimeUnit

import configuration.{KafkaConsumerConfig, KafkaConfig}
import kafka.consumer.{ConsumerConfig, Consumer, Whitelist}
import kafka.serializer.{StringDecoder}
import KafkaMessageConsumer._
import scala.annotation.tailrec
import scala.concurrent.duration.{FiniteDuration, Duration}
import scala.util.Try

case class KafkaMessageConsumer(topics : String*) {
  private val topicFilter = new Whitelist(topics.mkString(","))

  private lazy val consumer = Consumer.create(new ConsumerConfig(config asProperty))
  private lazy val stream = consumer.createMessageStreamsByFilter(topicFilter, numStreams, stringDecoder, stringDecoder).head

  def readNext() = Try(stream.head.message()).toOption

  def grabInDuration(timeout : Duration = FiniteDuration(1, TimeUnit.SECONDS)) = {
    @tailrec
    def poll(windowEnd : Duration, messages: Seq[String]): Seq[String] = {
      if (!(nowInSeconds > windowEnd)) {
        val newMessage = Try(Seq(stream.head.message())).getOrElse(Seq.empty[String])
        poll(windowEnd, messages ++ newMessage)
      } else {
        messages
      }
    }
    poll(nowInSeconds + timeout, Seq.empty[String])
  }

  def commitOffset = consumer.commitOffsets

  def shutDown = consumer.shutdown()
}
object KafkaMessageConsumer{
  lazy val config = new KafkaConfig() with KafkaConsumerConfig
  val stringDecoder = new StringDecoder()
  val numStreams = 1
  def now = System.currentTimeMillis()
  def nowInSeconds = FiniteDuration(now, TimeUnit.MILLISECONDS)
}
