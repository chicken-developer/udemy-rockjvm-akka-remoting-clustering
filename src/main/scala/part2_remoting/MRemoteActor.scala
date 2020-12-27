package part2_remoting

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object MRemoteActor extends App{
  val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("part2_remoting/remoteActors.conf"))
  val localSimpleActor = localSystem.actorOf(Props[MSimpleActor], "localSimpleActor")

  localSimpleActor ! "hello, local actor!"

  //TODO: Sent a message to remote actor

  //Method 01: Actor selection:
  val remoteActorSelect = localSystem.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActor")
  remoteActorSelect ! "Hello from local"

  //Method 02: Resolve the actor selection to an actor ref
  import localSystem.dispatcher
  implicit val timeout = Timeout(3 second)
  val remoteActorRefFuture = remoteActorSelect.resolveOne()
  remoteActorRefFuture.onComplete {
    case Success(actorRef) =>
        actorRef ! "Hello from local, this case is in future"
    case Failure(exception) =>
      println("Fail to get resources")
  }


  //Method 03: Actor identification via messages
  /*
    * 1. Actor resolver will ask for an actor selection from the local actor system
    * 2. Send an Identify(42) to an actor selection
    * 3. The remote actor will AUTOMATICALLY response with ActorIdentity(42, actorRef)
    * 4. The actor resolver is free to use the remote actorRef
   */
  class ActorResolver extends Actor with ActorLogging {
    override def preStart(): Unit = {

    }
    override def receive: Receive = {
      ???
    }
  }
}

object MRemoteActor_Remote extends App {
  val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("part2_remoting/remoteActors.conf").getConfig("remoteSystem"))
  val remoteSimpleActor = remoteSystem.actorOf(Props[MSimpleActor], "remoteSimpleActor")
  remoteSimpleActor ! "Hello"

}
