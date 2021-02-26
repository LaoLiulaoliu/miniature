package example.response

case class ErrorDetail() {
  private var errors: Array[ErrorDetailEntity] = null

  def getErrors: Array[ErrorDetailEntity] = errors

  def setErrors(errors: Array[ErrorDetailEntity]): Unit = {
    this.errors = errors
  }

  override def toString: String = {
    val content: StringBuilder = new StringBuilder
    for (i <- errors)
      content.append(i.toString)
    content.toString()
  }

  case class ErrorDetailEntity() {
    private var errorType: String = null
    private var message: String = null
    private var exception: SubErrorDetailEntity = null

    def getErrorType: String = errorType

    def setErrorType(errorType: String): Unit = {
      this.errorType = errorType
    }

    def getMessage: String = message

    def setMessage(message: String): Unit = {
      this.message = message
    }

    override def toString: String = "ErrorDetailEntity [errorType=" + errorType + ", message=" + message + ", " + exception.toString + "]"
  }

  case class SubErrorDetailEntity() {
    private var errorCode: ErrorCode = null
    override def toString: String = errorCode.toString
  }

  case class ErrorCode() {
    private var code: String = null
    private var message: String = null

    def getCode: String = code

    def setCode(code: String): Unit = {
      this.code = code
    }

    def getMessage: String = message

    def setMessage(message: String): Unit = {
      this.message = message
    }

    override def toString: String = "ErrorCode=" + code + ", Errormessage=" + message
  }
}

