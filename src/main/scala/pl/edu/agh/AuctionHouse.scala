package pl.edu.agh

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.iq80.leveldb.util.FileUtils
import pl.edu.agh.actors._

object AuctionHouse extends App {

  val config = ConfigFactory.load()
  val system = ActorSystem("AuctionHouse", config.getConfig("auction-house").withFallback(config))
  val externalSystem = ActorSystem("AuctionPublisher", config.getConfig("auction-publisher").withFallback(config))

  val storageLocations = List(
    new File(system.settings.config.getString("akka.persistence.journal.leveldb.dir")),
    new File(config.getString("akka.persistence.snapshot-store.local.dir")))
  // remove the line below to make akka-persistence work
  storageLocations foreach FileUtils.deleteRecursively

  system.actorOf(Props[AuctionSearch], "auctionSearch")

  system.actorOf(Props(classOf[Seller], List("auction1", "auction2")), "seller1")
  system.actorOf(Props(classOf[Seller], List("auction3", "auction4")), "seller2")

  system.actorOf(Props(classOf[Buyer]), "buyer1")
  system.actorOf(Props(classOf[Buyer]), "buyer2")
  system.actorOf(Props(classOf[Buyer]), "buyer3")
  system.actorOf(Props(classOf[Buyer]), "buyer4")
  system.actorOf(Props(classOf[Buyer]), "buyer5")
  system.actorOf(Props(classOf[Buyer]), "buyer6")
  system.actorOf(Props(classOf[Buyer]), "buyer7")
  system.actorOf(Props(classOf[Buyer]), "buyer8")

  system.actorOf(Props[Notifier], "notifier")

  externalSystem.actorOf(Props[AuctionPublisher], "auctionPublisher")
}
