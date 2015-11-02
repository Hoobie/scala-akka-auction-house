package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.{Bid, Register}

class BuyerTest(_system: ActorSystem) extends TestKit(_system)
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  def this() = this(ActorSystem("BuyerTest"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A Buyer" must {
    "bid" in {
      val auctionSearch = system.actorOf(Props[AuctionSearch], "auctionSearch")
      auctionSearch ! Register("auction1")
      auctionSearch ! Register("auction2")
      auctionSearch ! Register("auction3")
      auctionSearch ! Register("auction4")

      system.actorOf(Props[Buyer])

      expectMsgAnyClassOf(classOf[Bid])
    }
  }
}
