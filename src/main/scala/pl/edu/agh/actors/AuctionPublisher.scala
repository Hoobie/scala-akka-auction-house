package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging}
import pl.edu.agh.messages.Notify

class AuctionPublisher extends Actor with ActorLogging {

  override def receive: Receive = {
    case Notify(auctionTitle, buyer, value) => log.info("Received notification: [{}, {}, {}].", auctionTitle, buyer, value)
  }
}
