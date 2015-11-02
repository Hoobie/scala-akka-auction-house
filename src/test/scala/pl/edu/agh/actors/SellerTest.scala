package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh._

import scala.concurrent.duration._

class SellerTest(_system: ActorSystem) extends TestKit(_system)
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  def this() = this(ActorSystem("SellerTest"))

  val auctionSearch = system.actorOf(Props[AuctionSearch], "auctionSearch")

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A Seller" must {
    "receive a sold notification" in {
      val proxy = TestProbe()
      system.actorOf(Props(new Seller(List("auction1", "auction2", "auction3", "auction4")) {

        override def receive: Receive = {
          case x => proxy.ref.forward(x)
        }
      }))

      Thread.sleep(200)
      system.actorOf(Props[Buyer])

      proxy.expectMsg(60.seconds, SoldNotification)
    }
  }
}
