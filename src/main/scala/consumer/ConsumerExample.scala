package consumer

/**
  * Created by markkeinhorster on 11.02.16.
  */
object ConsumerExample {
  def main(args: Array[String]): Unit = {
    val strConsumer = KafkaMessageConsumer("test")
    while(true) {
      val t = System.currentTimeMillis()
      val messages = strConsumer.chunk()
      println(System.currentTimeMillis()-t)
      println(s"messages obtained is ${messages}")
      Thread.sleep(500)
    }
  }
}
