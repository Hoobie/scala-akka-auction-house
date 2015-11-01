package pl.edu.agh.actors

import akka.actor.{Actor, ActorRef, FSM}
import pl.edu.agh._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

class Auction(title: String) extends Actor with FSM[State, Data] {

  private val MAX_BID_TIME: Int = 60
  private val MAX_DELETION_TIME: Int = 30

  def startBidTimer = context.system.scheduler.scheduleOnce(Random.nextInt(MAX_BID_TIME).seconds, self, BidTimeout)

  startBidTimer

  context.actorSelection("../../auctionSearch") ! Register(title)

  startWith(Created, CurrentBid(null, 0))

  when(Created) {
    case Event(Bid(buyer, value), currentBid: CurrentBid) if value > currentBid.value =>
      log.info("Higher bid: {}!", value)
      goto(Activated) using CurrentBid(buyer, value)
    case Event(BidTimeout, _) =>
      log.info("Auction ignored :(")
      goto(Ignored)
  }

  when(Ignored, stateTimeout = Random.nextInt(MAX_DELETION_TIME).second) {
    case Event(StateTimeout, _) =>
      log.info("Ignored auction deleted...")
      context.stop(self)
      stay()
    case Event(Restart, _) =>
      log.info("Auction restarted.")
      startBidTimer
      goto(Created)
  }

  when(Activated) {
    case Event(Bid(buyer, value), currentBid: CurrentBid) if value > currentBid.value =>
      log.info("Higher bid: {}!", value)
      stay() using CurrentBid(buyer, value)
    case Event(BidTimeout, currentBid: CurrentBid) =>
      log.info("Item sold for {} to {}!", currentBid.value, currentBid.buyer.toString())
      currentBid.buyer ! SoldNotification
      context.parent ! SoldNotification
      goto(Sold)
  }

  when(Sold, stateTimeout = Random.nextInt(MAX_DELETION_TIME).second) {
    case Event(StateTimeout, _) =>
      log.info("Sold auction deleted...")
      context.stop(self)
      stay()
  }

  whenUnhandled {
    case Event(e, s) =>
//      log.warning("Received unhandled message {} in state {}/{}", e, stateName, s)
      stay()
  }

  initialize()
}

sealed trait State

case object Created extends State

case object Ignored extends State

case object Activated extends State

case object Sold extends State

sealed trait Data

case class CurrentBid(buyer: ActorRef, value: BigDecimal) extends Data
