package pl.edu.agh.actors

import akka.actor.{Actor, ActorRef}
import pl.edu.agh._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

class Buyer() extends Actor {

  val eventualAuctionSearch: Future[ActorRef] = context.actorSelection("/user/auctionSearch").resolveOne(1.seconds)
  eventualAuctionSearch onSuccess {
    case auctionSearch => context.system.scheduler.scheduleOnce(1.seconds, auctionSearch, SearchRequest("auction" + (Random.nextInt(3) + 1)))
  }

  override def receive = {
    case SearchResponse(results: Iterable[ActorRef]) =>
      scheduleBids(results)
      context.become(receiveWithNotifications)
  }

  def receiveWithNotifications: Receive = {
    case SearchResponse(results: Iterable[ActorRef]) => scheduleBids(results)
    case SoldNotification => println("Dear Buyer, you bought " + sender().toString())
    case HigherBidNotification => println("Dear Buyer, someone raised your bid in " + sender().toString())
  }

  private def scheduleBids(results: Iterable[ActorRef]): Unit = {
    results.map(auction => {
      val value: BigDecimal = BigDecimal(Random.nextInt(100))
      context.system.scheduler.schedule(0.seconds, Random.nextInt(10).seconds, auction,
        Bid(self, value, value + Random.nextInt(10)))
    })
  }
}