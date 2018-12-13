package services.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import models.sqs.ExecutedTransferQueueMessage

// TODO: Have this and ScheduledTransferQueueService both extend an abstract class QueueService that takes a
// parameter for sendMessage
class ExecutedTransferQueueService(amazonSQS: AmazonSQS, queueName: String) {
  val queueUrl: String = amazonSQS.createQueue(queueName).getQueueUrl

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)

  def sendMessage(executedTransferQueueMessage: ExecutedTransferQueueMessage): String = {
    val message = new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withMessageBody(mapper.writeValueAsString(executedTransferQueueMessage))

    amazonSQS.sendMessage(message).getMessageId
  }
}
