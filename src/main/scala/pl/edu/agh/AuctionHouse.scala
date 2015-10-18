package pl.edu.agh

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import pl.edu.agh.actors.{Auction, Buyer}

object AuctionHouse {

  val system = ActorSystem()
  val log = Logging(system, AuctionHouse.getClass.getName)

  def main(args: Array[String]) {
    log.debug("Initializing auction house.")

    val auction1 = system.actorOf(Props[Auction], "auction1")
    val auction2 = system.actorOf(Props[Auction], "auction2")
    val auction3 = system.actorOf(Props[Auction], "auction3")

    system.actorOf(Props(classOf[Buyer], List(auction1, auction2)), "buyer1")
    system.actorOf(Props(classOf[Buyer], List(auction2, auction3)), "buyer2")
    system.actorOf(Props(classOf[Buyer], List(auction1, auction3)), "buyer3")
  }
}
