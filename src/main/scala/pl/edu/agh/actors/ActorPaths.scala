package pl.edu.agh.actors

object ActorPaths {
  val MasterSearchPath = "/user/masterSearch"

  val NotifierPath = "/user/notifier"

  val AuctionPublisherRemotePath = "akka.tcp://AuctionPublisher@127.0.0.1:2553/user/auctionPublisher"
}
