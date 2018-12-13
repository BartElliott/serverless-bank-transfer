package models.sqs

import java.util.UUID

import org.joda.time.DateTime
import transfer.schedule.Request

case class ExecutedTransferQueueMessage(id: UUID, timeExecuted: DateTime, request: Request) {
  def this(request: ScheduledTransferQueueMessage) = this(request.id, DateTime.now(), request.request)
}
