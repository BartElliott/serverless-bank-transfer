package services.dynamo

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import models.dynamo.Balances
import com.gu.scanamo._
import com.gu.scanamo.syntax._

class BalancesService(client: AmazonDynamoDB) {
  val balanceTable: Table[Balances] = Table[Balances]("balances")

  def get(username: String): Option[Balances] = {
    Scanamo.exec(client)(balanceTable.get('username -> username)).flatMap(_.toOption)
  }

  def put(balances: Balances): Option[Balances] = {
    Scanamo.exec(client)(balanceTable.put(balances)).flatMap(_.toOption)
  }
}
