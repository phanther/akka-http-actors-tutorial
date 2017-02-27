package example.akka.http

import akka.actor.{Actor, Props}

/**
  * Created by phanther on 2/26/17.
  */
object ActorHandler {
  def props(): Props = {
    Props(classOf[ActorHandler])
  }
}

class ActorHandler extends Actor {
  def receive = {
    case inp: CustomerInput => {
      sender() ! CustomerOutput(inp.id, "John Doe", "123-456-7890")
    }
    case _ => sender() ! "DEFAULT RESPONSE"
  }
}
