package producer

object ProducerExample {
  def main(args: Array[String]): Unit = {
    val strProducer = KafkaMessageProducer[String]("test")
    (1 to 500000).foreach{
      x =>
        strProducer.sendMessage(x.toString)
        println(x)
        Thread.sleep(500)
      }
    }
}
