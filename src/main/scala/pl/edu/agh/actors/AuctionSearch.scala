package pl.edu.agh.actors

import akka.actor.{Actor, ActorRef}
import pl.edu.agh.{Register, SearchRequest, SearchResponse}

import scala.collection.mutable

class AuctionSearch extends Actor {

  val auctions = new mutable.HashMap[String, ActorRef]

  override def receive: Receive = {
    case Register(title: String) => auctions += (title -> sender)
    case SearchRequest(titlePart: String) =>
      sender ! SearchResponse(auctions.filter(_._1.contains(titlePart)).values.toList)
  }
}