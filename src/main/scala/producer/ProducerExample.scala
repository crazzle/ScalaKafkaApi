package producer

object ProducerExample {
  def main(args: Array[String]): Unit = {
    val strProducer = KafkaMessageProducer[String]("test")
    while(true){
      scala.io.StdIn.readLine()
      for(x <- 1 to 5){
        print("ping...")
        val r = scala.util.Random
        val num = r.nextInt(10).toString
        print("num: " + num + "...")
        strProducer.sendMessage(num)
        println("done.")
      }
    }
  }
}
