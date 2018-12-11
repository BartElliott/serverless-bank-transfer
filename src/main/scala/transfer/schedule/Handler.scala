package transfer.schedule

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.collection.JavaConverters

class Handler extends RequestHandler[Request, Response] {

  def handleRequest(input: Request, context: Context): Response = {
    Response("Hello from transfer.create", input)
  }
}

class ApiGatewayHandler extends RequestHandler[Request, ApiGatewayResponse] {

  def handleRequest(input: Request, context: Context): ApiGatewayResponse = {
    val headers: Map[String, Object] = Map()
    ApiGatewayResponse(200, "Hello from transfer.create API Gateway Handler!",
      JavaConverters.mapAsJavaMap[String, Object](headers),
      base64Encoded = true)
  }
}
