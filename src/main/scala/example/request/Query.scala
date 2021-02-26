package example.request

case class Query() {
  private var query: String = null
  private var arguments: Arguments = new Arguments

  def this(query: String, start: Long, end: Long) {
    this()
    this.query = query
    setArguments(start, end)
  }

  def addQuery(sql: String): Query = {
    this.query = sql
    this
  }

  def getQuery: String = query

  def setQuery(query: String): Unit = {
    this.query = query
  }

  def getArguments: Arguments = arguments

  def setArguments(start: Long, end: Long): Query = {
    this.arguments.setStart(start)
    this.arguments.setEnd(end)
    this
  }
}
