package configuration

import java.util.Properties

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import scala.collection.breakOut

class KafkaConfig{
  def config = ConfigFactory.load()

  lazy val asMap : Map[String, Object] = config.entrySet()
    .map(entry => (entry.getKey(), config.getString(entry.getKey())))(breakOut)

  lazy val asProperty : Properties = config.entrySet()
    .foldLeft(new Properties())((acc, el) => {
      acc.setProperty(el.getKey(), config.getString(el.getKey()))
      acc
    })
}

trait KafkaConsumerConfig extends KafkaConfig{
  abstract override def config = super.config.getConfig("consumer")
}

trait KafkaProducerConfig extends KafkaConfig{
  abstract override def config = super.config.getConfig("producer")
}