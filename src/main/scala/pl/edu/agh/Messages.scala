package pl.edu.agh

import akka.actor.ActorRef

sealed trait AuctionMessage

sealed trait UserMessage

sealed trait SearchMessage

case object Start extends AuctionMessage

case object BidTimeout extends AuctionMessage

case object Restart extends AuctionMessage

case class Bid(buyer: ActorRef, value: BigDecimal, maxValue: BigDecimal) extends AuctionMessage

case object HigherBidNotification extends UserMessage

case object SoldNotification extends UserMessage

case class Register(title: String) extends SearchMessage

case class SearchRequest(titlePart: String) extends SearchMessage

case class SearchResponse(results: List[ActorRef]) extends SearchMessage

