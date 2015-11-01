package pl.edu.agh

import akka.actor.ActorRef

sealed trait AuctionMessage

sealed trait UserMessage

sealed trait SearchMessage

case class Start() extends AuctionMessage

case class BidTimeout() extends AuctionMessage

case class Restart() extends AuctionMessage

case class Bid(buyer: ActorRef, value: BigDecimal, maxValue: BigDecimal) extends AuctionMessage

case class HigherBidNotification() extends UserMessage

case class SoldNotification() extends UserMessage

case class Register(title: String) extends SearchMessage

case class SearchRequest(titlePart: String) extends SearchMessage

case class SearchResponse(results: Iterable[ActorRef]) extends SearchMessage

