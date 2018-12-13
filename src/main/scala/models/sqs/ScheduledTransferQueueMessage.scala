package models.sqs

import java.util.UUID

import transfer.schedule.Request

case class ScheduledTransferQueueMessage(id: UUID, request: Request, delayedSeconds: Int)
