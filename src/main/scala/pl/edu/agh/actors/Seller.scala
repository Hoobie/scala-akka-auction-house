package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.github.nscala_time.time.Imports._
import pl.edu.agh.SoldNotification

import scala.util.Random

class Seller(auctionNames: Seq[String]) extends Actor with ActorLogging {

  private val MAX_AUCTION_DURATION_SECONDS: Int = 30
  private val endTime: DateTime = DateTime.now().plusSeconds(Random.nextInt(MAX_AUCTION_DURATION_SECONDS) + 1)
  auctionNames.foreach(auctionName => context.actorOf(Props(classOf[Auction], auctionName, endTime), auctionName))

  override def receive: Receive = {
    case SoldNotification =>
      log.info("Notification: you sold: {}", sender())
  }
}