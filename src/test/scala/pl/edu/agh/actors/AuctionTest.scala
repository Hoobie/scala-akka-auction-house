package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.Bid

class AuctionTest(_system: ActorSystem) extends TestKit(_system)
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  def this() = this(ActorSystem("AuctionTest"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "An Auction" must {
    "change state to Activated" in {
      val auction = TestFSMRef(new Auction("title"))
      assert(auction.stateName == Created)
      assert(auction.stateData == CurrentBid(null, 0, 0))

      auction ! Bid(self, 10, 10)

      assert(auction.stateName == Activated)
      assert(auction.stateData == CurrentBid(self, 10, 10))
    }
  }
}
