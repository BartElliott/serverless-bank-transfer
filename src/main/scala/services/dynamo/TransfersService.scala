package services.dynamo

import java.util.UUID

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult
import models.dynamo.Transfers
import com.gu.scanamo._
import com.gu.scanamo.syntax._
import org.joda.time.{DateTime, DateTimeZone}

class TransfersService(client: AmazonDynamoDB) {
  // This is needed for scanamo to understand the Transfers Datetimes
  implicit val jodaStringFormat = DynamoFormat.coercedXmap[DateTime, String, IllegalArgumentException](
    DateTime.parse(_).withZone(DateTimeZone.UTC)){_.toString}

  val transfersTable: Table[Transfers] = Table[Transfers]("transfers")

  def get(id: String): Option[Transfers] = {
    Scanamo.exec(client)(transfersTable.get('id -> id)).flatMap(_.toOption)
  }

  def put(transfers: Transfers): Option[Transfers] = {
    Scanamo.exec(client)(transfersTable.put(transfers)).flatMap(_.toOption)
  }

  def delete(id: UUID): DeleteItemResult = {
    Scanamo.exec(client)(transfersTable.delete('id -> id.toString))
  }
}
