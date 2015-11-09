package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import pl.edu.agh._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

class Buyer extends Actor with ActorLogging {

  private val MAX_BID_DELAY: Int = 10
  private val MAX_BID_VALUE: Int = 100
  private val AUCTIONS_COUNT: Int = 4

  private val eventualAuctionSearch: Future[ActorRef] = context.actorSelection("/user/auctionSearch").resolveOne(1.second)
  eventualAuctionSearch onSuccess {
    case auctionSearch =>
      val title = "auction" + (Random.nextInt(AUCTIONS_COUNT - 1) + 1)
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
      val value = BigDecimal(Random.nextInt(MAX_BID_VALUE))
      val duration = Random.nextInt(MAX_BID_DELAY).seconds
      context.system.scheduler.schedule(duration, duration, auction, BidCommand(self, value, value + Random.nextInt(MAX_BID_VALUE)))
    })
  }
}