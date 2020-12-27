package part2_remoting

import akka.actor.{Actor, ActorLogging}

class MSimpleActor extends Actor with ActorLogging{
  override def receive: Receive = {
    case message =>
      log.info(s"Received a message: $message from ${sender()}")
  }
}
