package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import pl.edu.agh.messages.Notify

import scala.concurrent.Await
import scala.concurrent.duration._

class NotifierRequest extends Actor with ActorLogging {

  implicit val timeout = Timeout(3.seconds)

  override def receive: Receive = {
    case n: Notify =>
      val future = context.actorSelection(ActorPaths.AuctionPublisherRemotePath) ? n
      Await.result(future, timeout.duration)
  }
}
