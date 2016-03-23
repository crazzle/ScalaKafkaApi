package configuration

import java.util.Properties

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import scala.collection.breakOut

class KafkaConfig(key : String){
  def config = ConfigFactory.load().getConfig(key)

  lazy val asProperty : Properties = config.entrySet()
    .foldLeft(new Properties())((acc, el) => {
      acc.setProperty(el.getKey(), config.getString(el.getKey()))
      acc
    })
}