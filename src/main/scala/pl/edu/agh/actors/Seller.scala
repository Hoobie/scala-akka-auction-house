package pl.edu.agh.actors

import akka.actor.{Actor, Props}
import pl.edu.agh.SoldNotification

class Seller(auctionNames: List[String]) extends Actor {

  auctionNames.foreach(auctionName => context.actorOf(Props(classOf[Auction], auctionName), auctionName))

  override def receive: Receive = {
    case SoldNotification =>
      println("Dear Seller, you sold " + sender().toString())
  }
}