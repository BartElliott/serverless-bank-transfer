package models.dynamo

import java.util.UUID

import org.joda.time.DateTime

case class Transfers(id: UUID, status: String, fromUserName: String, toUserName: String, amount: Double, currencyCode: String,
                dateScheduled: DateTime, dateCreated: DateTime = DateTime.now(), dateLastModified: DateTime = DateTime.now())
