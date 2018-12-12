package transfer.schedule

import java.io.{InputStream, OutputStream}
import java.util.UUID

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.JavaConverters

class ApiGatewayHandler extends RequestStreamHandler {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)

  def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val apiGatewayRequest: ApiGatewayRequest = mapper.readValue(input, classOf[ApiGatewayRequest])
    val req: Request = mapper.readValue(apiGatewayRequest.body, classOf[Request])
    val headers: Map[String, Object] = Map()

    val resp = Response(UUID.randomUUID().toString, req)
    
    val apiResponse = ApiGatewayResponse(200, mapper.writeValueAsString(resp),
      JavaConverters.mapAsJavaMap[String, Object](headers),
      base64Encoded = true)

    mapper.writeValue(output, apiResponse)
  }
}
