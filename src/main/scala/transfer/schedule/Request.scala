package transfer.schedule

import scala.beans.BeanProperty

case class Request(@BeanProperty var key1: String, @BeanProperty var key2: String, @BeanProperty var key3: String) {
  def this() = this("", "", "")
}
