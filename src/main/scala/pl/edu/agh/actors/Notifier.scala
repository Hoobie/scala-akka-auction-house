package pl.edu.agh.actors

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import pl.edu.agh.messages.Notify

class Notifier extends Actor with ActorLogging {

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: IllegalStateException =>
        log.error("Notification exception.")
        Restart
      case _: Exception => Stop
    }

  override def receive: Receive = {
    case notification: Notify => context.actorOf(Props(classOf[NotifierRequest], notification))
  }
}
