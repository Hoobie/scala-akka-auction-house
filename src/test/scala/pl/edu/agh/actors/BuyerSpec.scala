package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.InMemoryJournalSpec
import pl.edu.agh.messages.{BidCommand, Register, SoldNotification}

import scala.concurrent.duration._

class BuyerSpec extends InMemoryJournalSpec
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  val masterSearch = system.actorOf(Props[MasterSearch], "masterSearch")

  "A Buyer" must {
    "bid" in {
      masterSearch ! Register("auction1")
      masterSearch ! Register("auction2")
      masterSearch ! Register("auction3")
      masterSearch ! Register("auction4")

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
