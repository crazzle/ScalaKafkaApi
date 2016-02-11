package consumer

import java.util.concurrent.TimeUnit

import configuration.{KafkaConsumerConfig, KafkaConfig}
import kafka.consumer.{ConsumerConfig, Consumer, Whitelist}
import kafka.serializer.{StringDecoder}
import KafkaMessageConsumer._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

case class KafkaMessageConsumer(topics : String*) {
  private val topicFilter = new Whitelist(topics.mkString(","))

  lazy val consumer = Consumer.create(new ConsumerConfig(config asProperty))
  lazy val stream = consumer.createMessageStreamsByFilter(topicFilter, numStreams, stringDecoder, stringDecoder).head

  def read() : Stream[String] = Stream.cons(stream.head.message(), read())

  case class PollConfig(pollTimeout : Long)

  def chunk(timeout : Long = 4) = {
    @tailrec
    def poll(pollConfig: PollConfig, messages: Seq[String]): Seq[String] = {
      val isTimeLapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) > pollConfig.pollTimeout
      //val hasNext = stream.queue.remainingCapacity()
      if (!isTimeLapsed) {
        val newMessages = Try(stream.head.message()) match {
          case Success(suck) => Seq(suck)
          case Failure(ex) => Seq.empty[String]
        }
        poll(pollConfig, messages ++ newMessages)
      } else {
        messages
      }
    }
    val nowSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
    val messages = poll(PollConfig(nowSeconds + timeout), Seq.empty[String])
    consumer.commitOffsets
    messages
  }
}
object KafkaMessageConsumer{
  lazy val config = new KafkaConfig() with KafkaConsumerConfig
  val stringDecoder = new StringDecoder()
  val numStreams = 1
}
