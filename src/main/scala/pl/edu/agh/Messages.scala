package pl.edu.agh

import akka.actor.ActorRef

sealed trait AuctionMessage

sealed trait BuyerMessage

case class Start() extends AuctionMessage

case class BidTimeout() extends AuctionMessage

case class Restart() extends AuctionMessage

case class Bid(buyer: ActorRef, value: BigDecimal) extends AuctionMessage

case class SoldNotification() extends BuyerMessage
