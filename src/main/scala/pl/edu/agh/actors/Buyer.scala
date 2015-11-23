package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import pl.edu.agh.messages._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

class Buyer extends Actor with ActorLogging {

  private val MaxBidDelay = 10
  private val MaxBidValue = 100
  private val AuctionsCount = 4

  private val eventualAuctionSearch: Future[ActorRef] = context.actorSelection(ActorPaths.AuctionSearchPath).resolveOne(1.second)
  eventualAuctionSearch onSuccess {
    case auctionSearch =>
      val title = "auction" + (Random.nextInt(AuctionsCount - 1) + 1)
      log.debug("Buyer is searching for an auction: {}", title)
      context.system.scheduler.scheduleOnce(1.second, auctionSearch, SearchRequest(title))
  }

  override def receive = {
    case SearchResponse(results: Seq[ActorRef]) =>
      scheduleBids(results)
      context.become(receiveWithNotifications)
  }

  def receiveWithNotifications: Receive = {
    case SearchResponse(results: Seq[ActorRef]) => scheduleBids(results)
    case SoldNotification => log.info("Notification: you bought {}", sender())
    case HigherBidNotification => log.info("Notification: bid raised in: {}",  sender())
  }

  private def scheduleBids(results: Seq[ActorRef]): Unit = {
    log.debug("Found auctions: {}", results)
    results.map(auction => {
      val value = BigDecimal(Random.nextInt(MaxBidValue))
      val duration = Random.nextInt(MaxBidDelay).seconds
      context.system.scheduler.schedule(1.second, duration, auction, BidCommand(self, value, value + Random.nextInt(MaxBidValue)))
    })
  }
}