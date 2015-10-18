package pl.edu.agh.actors

import akka.actor.{Actor, ActorRef}
import pl.edu.agh.{Bid, SoldNotification}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

class Buyer(auctions: List[ActorRef]) extends Actor {

  val cancellableList = auctions.map(auction =>
    context.system.scheduler.schedule(0.seconds, Random.nextInt(10).seconds, auction, Bid(self, BigDecimal(Random.nextInt(100)))))

  override def receive: Receive = {
    case SoldNotification =>
      println("Sold from {} received!", sender())
  }
}