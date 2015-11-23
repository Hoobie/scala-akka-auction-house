package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.messages.{Register, SearchRequest, SearchResponse}

class AuctionSearchTest(_system: ActorSystem) extends TestKit(_system)
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  def this() = this(ActorSystem("AuctionSearchTest"))

  val auctionSearch = system.actorOf(Props[AuctionSearch], "auctionSearch")

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "An AuctionSearch" must {
    "register an auction and send a response" in {
      auctionSearch ! Register("interesting auction")

      auctionSearch ! SearchRequest("auction")

      expectMsg(SearchResponse(List(self)))
    }
  }
}
