package pl.edu.agh.actors

import akka.actor.{Actor, ActorLogging, Props}

class Notifier extends Actor with ActorLogging {

  override def receive: Receive = {
    case notification =>
      val request = context.actorOf(Props[NotifierRequest])
      request ! notification
  }
}
