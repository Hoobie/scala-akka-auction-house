package pl.edu.agh.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import pl.edu.agh.exception.NotificationException
import pl.edu.agh.messages.Notify

class Notifier extends Actor with ActorLogging {

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3) {
      case NotificationException(notification) =>
        log.error("Notification exception.")
        sendNotification(notification)
        Stop
      case _: Exception => Stop
    }

  override def receive: Receive = {
    case notification: Notify => sendNotification(notification)
  }

  private def sendNotification(notification: Notify): Unit = {
    context.actorOf(Props[NotifierRequest]) ! notification
  }
}
