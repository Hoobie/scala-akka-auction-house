package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.InMemoryJournalSpec
import pl.edu.agh.messages.SoldNotification

import scala.concurrent.duration._

class SellerSpec extends InMemoryJournalSpec
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  val masterSearch = system.actorOf(Props[MasterSearch], "masterSearch")

  "A Seller" must {
    "receive a sold notification" in {
      val proxy = TestProbe()
      system.actorOf(Props(new Seller(List("auction1", "auction2", "auction3", "auction4")) {

        override def receive: Receive = {
          case x => proxy.ref.forward(x)
        }
      }))

      system.actorOf(Props[Buyer])

      proxy.expectMsg(60.seconds, SoldNotification)
    }
  }
}
