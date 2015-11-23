package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging}
import pl.edu.agh.messages.Notify

class NotifierRequest extends Actor with ActorLogging {

  private val publisher = context.actorSelection(ActorPaths.AuctionPublisherRemotePath)

  override def receive: Receive = {
    case notification: Notify => publisher ! notification
  }
}
