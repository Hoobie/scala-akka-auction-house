package pl.edu.agh.actors

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import com.github.nscala_time.time.Imports._
import pl.edu.agh.messages._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.reflect._

class Auction(title: String, baseEndTime: DateTime) extends PersistentFSM[State, Data, AuctionEvent] {

  private val DeletionSeconds = 5

  private val notifier = context.actorSelection(ActorPaths.NotifierPath)

  override def persistenceId: String = title

  override def domainEventClassTag: ClassTag[AuctionEvent] = classTag[AuctionEvent]

  def startBidTimer(duration: Duration) = context.system.scheduler.scheduleOnce(
    FiniteDuration(duration.seconds, TimeUnit.SECONDS), self, BidTimeout)

  startWith(Uninitialized, EmptyData)

  when(Uninitialized) {
    case Event(StartCommand, _) =>
      goto(Created) applying StartEvent(baseEndTime) andThen {
        _ => log.info("Auction created.")
      }
  }

  when(Created) {
    case Event(BidCommand(buyer, value, maxValue), _) if value > 0 =>
      goto(Activated) applying BidEvent(buyer, value, maxValue) andThen {
        _ =>
          log.info("First bid: {}!", value)
          notifier ! Notify(title, buyer, value)
      }
    case Event(BidTimeout, _) =>
      goto(Ignored) andThen {
        _ => log.info("Auction ignored :(")
      }
  }

  when(Ignored, stateTimeout = FiniteDuration(DeletionSeconds, TimeUnit.SECONDS)) {
    case Event(StateTimeout, _) =>
      log.info("Auction deleted.")
      stop()
    case Event(RestartCommand, _) =>
      goto(Created) applying StartEvent(baseEndTime) andThen {
        _ => log.info("Auction restarted.")
      }
  }

  when(Activated) {
    case Event(BidCommand(buyer, value, maxValue), bidData: BidData) if value > bidData.maxValue =>
      stay applying BidEvent(buyer, value, maxValue) andThen {
        _ =>
          log.info("Bid raised: {}!", value)
          bidData.buyer ! HigherBidNotification
          notifier ! Notify(title, buyer, value)
      }
    case Event(BidCommand(_, value, _), bidData: BidData) if value > bidData.value =>
      stay applying BidEvent(bidData.buyer, value, bidData.maxValue) andThen {
        _ =>
          log.info("Bid bumped up to: {}!", value)
          bidData.buyer ! HigherBidNotification
          notifier ! Notify(title, bidData.buyer, value)
      }
    case Event(BidTimeout, bidData: BidData) =>
      goto(Sold) andThen {
        _ =>
          log.info("Item sold for {} to {}!", bidData.value, bidData.buyer)
          bidData.buyer ! SoldNotification
          context.parent ! SoldNotification
      }
  }

  when(Sold, stateTimeout = FiniteDuration(DeletionSeconds, TimeUnit.SECONDS)) {
    case Event(StateTimeout, _) =>
      log.info("Auction deleted.")
      stop()
  }

  whenUnhandled {
    case Event(_, _) =>
      stay()
  }

  initialize()

  self ! StartCommand

  override def applyEvent(event: AuctionEvent, data: Data): Data = {
    event match {
      case StartEvent(endTime) =>
        context.actorSelection("/user/auctionSearch") ! Register(title)

        val now: DateTime = DateTime.now()
        if (now.isBefore(endTime)) {
          val duration: Duration = (now to endTime).toDuration
          log.info("Bid timeout: {} seconds", duration.seconds)
          startBidTimer(duration)
        }
        StartupData(endTime)

      case BidEvent(buyer, value, maxValue) =>
        BidData(buyer, value, maxValue)
    }
  }
}

sealed trait State extends FSMState

case object Uninitialized extends State {
  override def identifier: String = "uninitialized"
}

case object Created extends State {
  override def identifier: String = "created"
}

case object Ignored extends State {
  override def identifier: String = "ignored"
}

case object Activated extends State {
  override def identifier: String = "activated"
}

case object Sold extends State {
  override def identifier: String = "sold"
}

sealed trait Data

case object EmptyData extends Data

case class StartupData(endTime: DateTime) extends Data

case class BidData(buyer: ActorRef, value: BigDecimal, maxValue: BigDecimal) extends Data

sealed trait AuctionEvent

case class StartEvent(endTime: DateTime) extends AuctionEvent

case class BidEvent(buyer: ActorRef, value: BigDecimal, maxValue: BigDecimal) extends AuctionEvent
