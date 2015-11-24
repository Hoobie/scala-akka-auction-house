package pl.edu.agh.actors

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.messages.{Register, SearchRequest, SearchResponse}

class MasterSearchSpec(_system: ActorSystem) extends TestKit(_system)
with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  def this() = this(ActorSystem("MasterSearchSpec"))

  val masterSearch = system.actorOf(Props[MasterSearch], "masterSearch")

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A MasterSearch" must {
    "register an auction and send a response" in {
      masterSearch ! Register("interesting auction")

      masterSearch ! SearchRequest("auction")

      expectMsg(SearchResponse(List(self)))
    }
  }
}
