package consumer

/**
  * Created by markkeinhorster on 11.02.16.
  */
object ConsumerExample {
  def main(args: Array[String]): Unit = {
    val strConsumer = KafkaMessageConsumer("test")
    while(true){
      println("read: " + strConsumer.read().head)
    }
  }
}
