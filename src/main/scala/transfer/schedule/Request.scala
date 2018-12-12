package transfer.schedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime

case class Request(@JsonProperty fromUser: String,
                   @JsonProperty toUser: String,
                   @JsonProperty currencyCode: String,
                   @JsonProperty amount: Double,
                   @JsonProperty scheduledTime: DateTime = DateTime.now()
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
