package pl.edu.agh

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import pl.edu.agh.actors.{AuctionSearch, Buyer, Seller}

object AuctionHouse {

  val system = ActorSystem()
  val log = Logging(system, AuctionHouse.getClass.getName)

  def main(args: Array[String]) {
    log.info("Initializing auction house.")

    system.actorOf(Props(classOf[Seller], List("auction1", "auction2")), "seller1")
    system.actorOf(Props(classOf[Seller], List("auction3", "auction4")), "seller2")

    system.actorOf(Props[AuctionSearch], "auctionSearch")

    Thread.sleep(100)

    system.actorOf(Props(classOf[Buyer]), "buyer1")
    system.actorOf(Props(classOf[Buyer]), "buyer2")
    system.actorOf(Props(classOf[Buyer]), "buyer3")
  }
}
