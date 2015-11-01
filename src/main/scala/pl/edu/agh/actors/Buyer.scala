package pl.edu.agh.actors

import akka.actor.{Actor, ActorRef}
import pl.edu.agh.{Bid, SearchRequest, SearchResponse, SoldNotification}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

class Buyer() extends Actor {

  context.actorSelection("../auctionSearch") ! SearchRequest("auction" + (Random.nextInt(3) + 1))

  override def receive: Receive = {
    case SearchResponse(results: Iterable[ActorRef]) => results.map(auction =>
      context.system.scheduler.schedule(0.seconds, Random.nextInt(10).seconds, auction, Bid(self, BigDecimal(Random.nextInt(100)))))
    case SoldNotification =>
      println("You bought {}!", sender())
  }
}