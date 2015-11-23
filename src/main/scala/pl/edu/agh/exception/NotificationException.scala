package pl.edu.agh.exception

import pl.edu.agh.messages.Notify

case class NotificationException(notification: Notify) extends RuntimeException("Exception due to message")
