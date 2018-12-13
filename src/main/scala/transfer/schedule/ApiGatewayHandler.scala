package transfer.schedule

import java.io.{InputStream, OutputStream}
import java.util.UUID

import exceptions._
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import models.dynamo.{Balances, Transfers}
import models.sqs.ScheduledTransferQueueMessage
import org.joda.time.{DateTime, Seconds}
import services.dynamo.{BalancesService, TransfersService}
import services.sqs.ScheduledTransferQueueService

import scala.collection.JavaConverters

/* TODO: Figure out why the lambda was erroring when we extend the generic RequestHandler and use that so we don't have
 * to deal with the inputstream
 */
class ApiGatewayHandler extends RequestStreamHandler {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)

  // TODO: Need to be able to handle errors here if services are unavailable
  val dynamoClient: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
  val sqsClient: AmazonSQS = AmazonSQSClientBuilder.defaultClient()

  val balancesService: BalancesService = new BalancesService(dynamoClient)
  val transfersService: TransfersService = new TransfersService(dynamoClient)
  /* TODO: Use the environment variables that are exported in the serverless.yml file
   * so we don't have to hardcode names of resources like "scheduled-transfers"
   */
  val scheduledTransfersQueueService: ScheduledTransferQueueService =
    new ScheduledTransferQueueService(sqsClient, "scheduled-transfers")


  // TODO: Split the business logic out of this function and into its own place
  def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val apiGatewayRequest: ApiGatewayRequest = mapper.readValue(input, classOf[ApiGatewayRequest])
    val req: Request = mapper.readValue(apiGatewayRequest.body, classOf[Request])

    /* TODO: Use OAuth to validate that this request is from a client that is allowed to make requests for the fromUser
     * (and probably remove the 'fromUser' field from the request entirely)
     */
    validateRequest(req)

    val oldFromBalance = balancesService.get(req.fromUser) match {
      case Some(balances: Balances) => balances.availableBalance
      case None => throw new UserNotFoundException
    }

    val newFromBalanceAmount = oldFromBalance - req.amount
    val requestId = UUID.randomUUID()
    val newTransfer = Transfers(requestId, "scheduled", req.fromUser, req.toUser, req.amount, req.currencyCode,
      req.scheduledTime.getOrElse(DateTime.now))
    val newBalance = Balances(req.fromUser, newFromBalanceAmount, req.currencyCode)
    val delayedSeconds =
      req.scheduledTime match {
        case Some(scheduledTime) => Seconds.secondsBetween(scheduledTime, DateTime.now)
        case None => Seconds.ZERO
      }

    //TODO: if (delayedSeconds.getSeconds > MAX_DELAY) throw new InvalidDelayException -- what should MAX_DELAY be?

    scheduledTransfersQueueService.sendMessage(ScheduledTransferQueueMessage(requestId, req, delayedSeconds.getSeconds))
    transfersService.put(newTransfer)
    balancesService.put(newBalance)

    val resp = Response(requestId.toString, req)
    val apiResponse = ApiGatewayResponse(200, mapper.writeValueAsString(resp),
      JavaConverters.mapAsJavaMap[String, Object](Map()),
      base64Encoded = true)

    mapper.writeValue(output, apiResponse)
  }

  def validateRequest(req: Request): Unit = {
    /* TODO: validate the following:
        - Both toUser and fromUser exist
        - scheduledTime is in the future
          - should we allow requests that were created in the recent past?
            - e.g. if a client is retrying to send a request it failed to send the first few times?
        - Check currencyCode matches
          - do we need to support a database structure that allows people to have multiple currencies?
        - If the request has an ID that we already know about, return a successful "already exists" message
     */

    if (req.amount <= 0) throw new NegativeTransferException
    validateScheduledForTheFuture(req.scheduledTime.getOrElse(DateTime.now))

    val oldFromBalance = balancesService.get(req.fromUser) match {
      case Some(balances: Balances) => balances.availableBalance
      case None => throw new UserNotFoundException
    }

    validateUserHasRequiredBalance(oldFromBalance, req.amount, req.currencyCode)
  }

  def validateUserHasRequiredBalance(oldBalance: Double, requiredBalance: Double, currencyCode: String): Unit = {
    if (oldBalance < requiredBalance) throw new InsufficientBalanceException
  }

  def validateScheduledForTheFuture(scheduledTime: DateTime): Unit = {
    // TODO
  }

}
