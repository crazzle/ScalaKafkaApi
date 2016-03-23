package consumer

import configuration.KafkaConfig
import kafka.consumer._
import kafka.serializer.{Decoder, StringDecoder}
import rx.lang.scala.Observable

import scala.concurrent.{ExecutionContext, Future, blocking}


object KafkaMessageConsumer {

  def apply(config: KafkaConfig, groupId : String): ConsumerConnector = {
    val properties = config.asProperty
    properties.setProperty("group.id", groupId)
    Consumer.create(new ConsumerConfig(properties))
  }

  implicit class KafkaConsumerOps(consumer: ConsumerConnector) {
    private val stringDecoder = new StringDecoder

    def subscribe(topics: String*): KafkaStream[String, String] = subscribe(stringDecoder, stringDecoder, topics: _*)

    def subscribe[KeyType, ValueType](keyDecoder: Decoder[KeyType], valueDecoder: Decoder[ValueType],
                                      topics: String*): KafkaStream[KeyType, ValueType] = {

      consumer.createMessageStreamsByFilter(topicFilter(topics), 1, keyDecoder, valueDecoder).head
    }

    def observe(topics: String*): Observable[String] = {
      observe(stringDecoder, stringDecoder, topics : _*)
    }

    def observe[KeyType, ValueType](keyDecoder: Decoder[KeyType], valueDecoder: Decoder[ValueType],
                                    topics: String*): Observable[ValueType] = {

      Observable.from(subscribe(keyDecoder, valueDecoder, topics: _*).iterator().toIterable).map(_.message())
    }

    private def topicFilter(topics: Seq[String]) = new Whitelist(topics.mkString(","))
  }

  implicit class KafkaStreamOps[KeyType, ValueType](stream: KafkaStream[KeyType, ValueType]) {
    def poll(implicit ec: ExecutionContext): Future[ValueType] = Future {
      blocking {
        stream.head.message()
      }
    }
  }
}