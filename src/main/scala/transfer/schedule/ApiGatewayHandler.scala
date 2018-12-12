package transfer.schedule

import java.io.{InputStream, OutputStream}
import java.util.UUID

import exceptions._
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.gu.scanamo.DynamoFormat
import models.dynamo.{Balances, Transfers}
import org.joda.time.{DateTime, DateTimeZone}
import services.dynamo.{BalancesService, TransfersService}

import scala.collection.JavaConverters

class ApiGatewayHandler extends RequestStreamHandler {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)


  val dynamoClient: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
  val balancesService: BalancesService = new BalancesService(dynamoClient)
  val transfersService: TransfersService = new TransfersService(dynamoClient)

  def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val apiGatewayRequest: ApiGatewayRequest = mapper.readValue(input, classOf[ApiGatewayRequest])
    val req: Request = mapper.readValue(apiGatewayRequest.body, classOf[Request])

    val validatedResult = validateRequest(req)

    val oldFromBalance = balancesService.get(req.fromUser) match {
      case Some(balances: Balances) => balances.availableBalance
      case None => throw new UserNotFoundException
    }

    val newFromBalanceAmount = oldFromBalance - req.amount
    val requestId = UUID.randomUUID()
    val newTransfer = Transfers(requestId, "scheduled", req.fromUser, req.toUser, req.amount, req.currencyCode, req.scheduledTime)
    val newBalance = Balances(req.fromUser, newFromBalanceAmount, req.currencyCode)
    transfersService.put(newTransfer)
    balancesService.put(newBalance)

    val resp = Response(requestId.toString, req)
    val apiResponse = ApiGatewayResponse(200, mapper.writeValueAsString(resp),
      JavaConverters.mapAsJavaMap[String, Object](Map()),
      base64Encoded = true)

    mapper.writeValue(output, apiResponse)
  }

  def validateRequest(req: Request): Unit = {
    validateScheduledForTheFuture(req.scheduledTime)
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

  }

}
