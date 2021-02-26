package example.request

case class Arguments() {
  private var startTime = 0L
  private var endTime = 0L
  private val engine = "tsdb"

  def getStart: Long = startTime

  def setStart(start: Long): Unit = {
    this.startTime = start
  }

  def getEnd: Long = endTime

  def setEnd(end: Long): Unit = {
    this.endTime = end
  }

  def getEngine: String = engine
}
