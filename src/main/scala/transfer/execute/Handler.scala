package transfer.execute

import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.amazonaws.services.lambda.runtime.Context
import models.sqs.{ExecutedTransferQueueMessage, ScheduledTransferQueueMessage}
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import models.dynamo.Balances
import services.dynamo.{BalancesService, TransfersService}
import services.sqs.ExecutedTransferQueueService

class ProcessSQSEvents [SQSEvent, Void] {}

class Handler extends RequestHandler[SQSEvent, Unit] {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val dynamoClient: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
  val sqsClient: AmazonSQS = AmazonSQSClientBuilder.defaultClient()

  val balancesService: BalancesService = new BalancesService(dynamoClient)
  val transfersService: TransfersService = new TransfersService(dynamoClient)

  val executedTransfersQueueService: ExecutedTransferQueueService = new ExecutedTransferQueueService(sqsClient, "executed-transfers")

  /* TODO
   * Check the request's status in the Transfers table
   * If 'scheduled':
   *   Remove the request from the scheduled Table
   *   Add the 'amount' to the 'toUser' in the Balances table
   *
   * If 'cancelled':
   *   Remove the request from the scheduled Table
   *   Refund the 'amount' back to the 'fromUser' in the Balance table
   *
   * If 'completed':
   *   Remove the request from the scheduled Table
   *
   */
  def handleRequest(input: SQSEvent, context: Context): Unit = {
    scala.collection.JavaConverters.asScalaBuffer(input.getRecords).map { sqsMessage =>
      val req: ScheduledTransferQueueMessage = mapper.readValue(sqsMessage.getBody, classOf[ScheduledTransferQueueMessage])

      // Regardless of the status, we want to remove the request from the scheduled Table
      transfersService.delete(req.id)

      updateToUserBalanceTable(req)

      val newMessage = new ExecutedTransferQueueMessage(req)
      executedTransfersQueueService.sendMessage(newMessage)
    }

  }

  private def updateToUserBalanceTable(req: ScheduledTransferQueueMessage): Option[Balances] = {
    // Update the toUser Balance
    val oldToBalances = balancesService.get(req.request.toUser).get
    val newBalanceAmount = oldToBalances.availableBalance + req.request.amount
    val newToBalance = Balances(oldToBalances.username, newBalanceAmount, oldToBalances.currencyCode)
    balancesService.put(newToBalance)
  }
}
