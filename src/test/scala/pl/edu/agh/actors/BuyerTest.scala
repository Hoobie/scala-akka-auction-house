package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.messages.{BidCommand, Register, SoldNotification}
import pl.edu.agh.spec.InMemoryJournalSpec

import scala.concurrent.duration._

class BuyerTest extends InMemoryJournalSpec
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  val auctionSearch = system.actorOf(Props[AuctionSearch], "auctionSearch")

  "A Buyer" must {
    "bid" in {
      auctionSearch ! Register("auction1")
      auctionSearch ! Register("auction2")
      auctionSearch ! Register("auction3")
      auctionSearch ! Register("auction4")

      system.actorOf(Props[Buyer])

      expectMsgAnyClassOf(classOf[BidCommand])
    }

    "get notified" in {
      system.actorOf(Props(classOf[Seller], List("auction1", "auction2", "auction3", "auction4")), "seller")

      val proxy = TestProbe()
      system.actorOf(Props(new Buyer() {
        override def receiveWithNotifications: Receive = {
          case x => proxy.ref.forward(x)
        }
      }))

      proxy.expectMsg(60.seconds, SoldNotification)
    }
  }
}
