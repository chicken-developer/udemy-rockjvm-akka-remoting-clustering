package part5_myremoting

import akka.actor.{Actor, ActorLogging}

class SimpleActor extends Actor with ActorLogging{
  override def receive: Receive = {
    case m: String => log.info(s"Get a message: $m  from sender: ${sender()}")
  }
}
