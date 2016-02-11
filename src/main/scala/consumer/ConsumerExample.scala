package consumer

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

/**
  * Created by markkeinhorster on 11.02.16.
  */
object ConsumerExample {
  def main(args: Array[String]): Unit = {
    val strConsumer = KafkaMessageConsumer("test")
    while(true) {
      val stream = strConsumer.grabInDuration(new FiniteDuration(2, TimeUnit.SECONDS))
      println(s"messages obtained is ${stream}")
      strConsumer.commitOffset
      Thread.sleep(500)
    }
  }
}
