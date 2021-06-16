package part5_myremoting

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSystem, Identify, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object RemoteActors extends App {

  val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("part5_myremoting/myRemoteActors.conf"))
  val localSimpleActor = localSystem.actorOf(Props[SimpleActor], "localSimpleActor")

  localSimpleActor ! "hello, local actor!"

  //====Send message to remote actor

  //Method 1 : Actor selection
  val remoteActorSelection = localSystem.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActor")
  remoteActorSelection ! "hello from local jvm"

  //Method 2: Resolve the actor selection to an actor ref
  import localSystem.dispatcher
  implicit val timeout = Timeout(3 seconds)
  val remoteActorRefFuture = remoteActorSelection.resolveOne()
  remoteActorRefFuture.onComplete{
    case Success(actorRef) =>
      actorRef ! "I resolve you in a future"
    case Failure(ex) =>
      println("I failed to resolve ")
  }

  //Method 3: Actor identification via message
  /*
    ActorResolver will ask for an actor selection from the local actor system
   */
  class ActorResolver extends Actor with ActorLogging {

  }
}

object RemoteActor_AnotherJVM extends App {
  val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("part5_myremoting/myRemoteActors.conf").getConfig("remoteSystem"))
  val remoteSimpleActor = remoteSystem.actorOf(Props[SimpleActor], "remoteSimpleActor")
  remoteSimpleActor ! "Hello"

}