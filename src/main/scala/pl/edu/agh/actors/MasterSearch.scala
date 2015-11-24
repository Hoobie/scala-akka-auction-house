package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, RoundRobinRoutingLogic, Router}
import pl.edu.agh.messages.{Register, SearchRequest}

class MasterSearch extends Actor with ActorLogging {

  val routees = Vector.fill(5) {
    val r = context.actorOf(Props[AuctionSearch])
    context watch r
    ActorRefRoutee(r)
  }

  val registerRouter = Router(BroadcastRoutingLogic(), routees)

  val searchRouter = Router(RoundRobinRoutingLogic(), routees)

  override def receive: Receive = {
    case r: Register =>
      log.info("Registering...")
      registerRouter.route(r, sender())
    case sr: SearchRequest =>
      searchRouter.route(sr, sender())
      log.info("Searching...")
  }
}
