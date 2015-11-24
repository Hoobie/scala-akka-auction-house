package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import pl.edu.agh.messages.{Register, SearchRequest, SearchResponse}

import scala.collection.mutable

class AuctionSearch extends Actor with ActorLogging {

  private val auctions = new mutable.HashMap[String, ActorRef]

  override def receive: Receive = {
    case Register(title: String) =>
      log.info("Registering title: {}", title)
      auctions += (title -> sender)
    case SearchRequest(titlePart: String) =>
      log.info("Searching for: {}", titlePart)
      sender ! SearchResponse(auctions.filter(_._1.contains(titlePart)).values.toList)
  }
}