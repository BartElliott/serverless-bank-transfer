package services.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.{CreateQueueRequest, SendMessageRequest}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import models.sqs.ScheduledTransferQueueMessage

class QueueService(amazonSQS: AmazonSQS, queueName: String) {
  val queueUrl: String = amazonSQS.createQueue(queueName).getQueueUrl

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)

  def sendMessage(scheduledTransferQueueMessage: ScheduledTransferQueueMessage): String = {
    val message = new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withDelaySeconds(scheduledTransferQueueMessage.delayedSeconds)
        .withMessageBody(mapper.writeValueAsString(scheduledTransferQueueMessage))

    amazonSQS.sendMessage(message).getMessageId
  }
}
