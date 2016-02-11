package consumer

/**
  * Created by markkeinhorster on 11.02.16.
  */
object ConsumerExample {
  def main(args: Array[String]): Unit = {
    val strConsumer = KafkaMessageConsumer("test")
    while(true) {
      val stream = strConsumer.readNext()
      println(s"messages obtained is ${stream}")
      strConsumer.commitOffset
      Thread.sleep(500)
    }
  }
}
