package configuration

import com.typesafe.config.ConfigFactory

import scala.collection.breakOut

class KafkaConfig{
  def configKeys : Seq[String] = Seq()
  lazy val config = ConfigFactory.load()
  lazy val asMap : Map[String, Object] = configKeys.filter(key => config.hasPath(key))
    .map(key => (key, config.getString(key)))(breakOut)
}

trait KafkaConsumerConfig extends KafkaConfig{
  val groupId = s"group.id"
  val zookeeperConnect = s"zookeeper.connect"

  abstract override def configKeys = Seq[String](
    groupId,
    zookeeperConnect)++super.configKeys
}

trait KafkaProducerConfig extends KafkaConfig{
  val brokers = s"metadata.broker.list"
  val serializer = s"serializer.class"
  val keyserializer = s"key.serializer"
  val valueserializer = s"value.serializer"
  val producertype = s"producer.type"
  val bootstrap = s"bootstrap.servers"

  abstract override def configKeys = Seq[String](
      brokers,
      serializer,
      producertype,
      bootstrap,
      keyserializer,
      valueserializer) ++ super.configKeys
}