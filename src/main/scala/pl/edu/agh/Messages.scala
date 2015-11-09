package pl.edu.agh

import akka.actor.ActorRef

sealed trait AuctionCommand

sealed trait UserMessage

sealed trait SearchMessage

case object StartCommand extends AuctionCommand

case object RestartCommand extends AuctionCommand

case class BidCommand(buyer: ActorRef, value: BigDecimal, maxValue: BigDecimal) extends AuctionCommand

case object BidTimeout extends AuctionCommand

case object HigherBidNotification extends UserMessage

case object SoldNotification extends UserMessage

case class Register(title: String) extends SearchMessage

case class SearchRequest(titlePart: String) extends SearchMessage

case class SearchResponse(results: Seq[ActorRef]) extends SearchMessage

