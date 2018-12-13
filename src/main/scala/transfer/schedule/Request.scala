package transfer.schedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime

/* TODO: This request from the client needs to have a UUID that we can use to de-dupe requests that are sent
 * multiple times from the client
 */
case class Request(@JsonProperty fromUser: String,
                   @JsonProperty toUser: String,
                   @JsonProperty currencyCode: String,
                   @JsonProperty amount: Double,
                   @JsonProperty scheduledTime: Option[DateTime]
                  )
case class ApiGatewayRequest(@JsonProperty resource: String,
                             @JsonProperty path: String,
                             @JsonProperty httpMethod: String,
                             @JsonProperty headers: Map[String, Object],
                             @JsonProperty multiValueHeaders: Map[String, Object],
                             @JsonProperty queryStringParameters: Map[String, Object],
                             @JsonProperty multiValueQueryStringParameters: Map[String, Object],
                             @JsonProperty pathParameters:  Map[String, Object],
                             @JsonProperty stageVariables: Map[String, Object],
                             @JsonProperty requestContext: Map[String, Object],
                             @JsonProperty body: String,
                             @JsonProperty isBase64Encoded: Boolean
                            )
