package transfer.schedule

import scala.beans.BeanProperty

case class Response(@BeanProperty transferId: String,
                    @BeanProperty request: Request)
