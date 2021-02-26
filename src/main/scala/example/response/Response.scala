package example.response

case class Response() {
  private var statusCode = 0
  private var errorDetail:ErrorDetail = null

  def this(statusCode: Int) {
    this()
    this.statusCode = statusCode
  }

  def isSuccess: Boolean = statusCode == 200 || statusCode == 204

  def getStatusCode: Int = statusCode

  def setStatusCode(statusCode: Int): Unit = {
    this.statusCode = statusCode
  }

  def getErrorDetail: ErrorDetail = errorDetail

  def setErrorDetail(errorDetail: ErrorDetail): Unit = {
    this.errorDetail = errorDetail
  }

  override def toString: String = "Response [statusCode=" + statusCode + ", errorDetail=" + errorDetail + "]"
}
