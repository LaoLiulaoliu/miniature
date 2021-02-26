package example.response

case class SimpleHttpResponse() {
  private var statusCode = 0
  private var content = ""
  private var errorDetail: ErrorDetail = null

  def isSuccess: Boolean = statusCode == 200 || statusCode == 204

  def getStatusCode: Int = statusCode

  def setStatusCode(statusCode: Int): Unit = {
    this.statusCode = statusCode
  }

  def getContent: String = content

  def setContent(content: String): Unit = {
    this.content = content
  }

  def getErrorDetail: ErrorDetail = errorDetail

  def setErrorDetail(errorDetail: ErrorDetail): Unit = {
    this.errorDetail = errorDetail
  }
}
