package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, Status}
import pl.edu.agh.exception.NotificationException
import pl.edu.agh.messages.Notify

class AuctionPublisher extends Actor with ActorLogging {

  private val MaxCounter: Int = 3

  private var counter = 0

  override def receive: Receive = {
    case n: Notify =>
      if (counter < MaxCounter) {
        log.info("Received notification: [{}, {}, {}].", n.auctionTitle, n.buyer, n.value)
        counter += 1
        sender() ! Status.Success
      } else {
        log.error("Error for notification: [{}, {}, {}].", n.auctionTitle, n.buyer, n.value)
        counter = 0
        sender() ! Status.Failure(new NotificationException(n))
      }
  }
}
