package example.http

import com.google.gson.Gson
import example.request.QueryBuilder
import example.response.{ErrorDetail, SimpleHttpResponse}
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.{LogManager, Logger}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

class HttpClientImpl extends HttpClient {
  private val logger: Logger = LogManager.getLogger(classOf[HttpClientImpl])

  var serviceUrl: String = null
  var mapper: Gson = new Gson
  private val httpClient = new PoolingHttpClient

  def HttpClientImpl(serviceUrl: String, accessToken: String = ""): Unit = {
    this.serviceUrl = buildUrl(serviceUrl, BATCH_QUERY_POST_API, accessToken)
  }

  private def buildUrl(serviceUrl: String, postApiEndPoint: String, accessToken: String = ""): String = {
    if (!StringUtils.isEmpty(accessToken)) {
      return s"$serviceUrl$postApiEndPoint?access_token=$accessToken"
    }
    serviceUrl + postApiEndPoint
  }

  /**
   * Based on charset to read stream content.
   *
   * @param ctype
   * minitype: application/json; charset=UTF-8
   * @return charset of response
   *
   */
  private def getResponseCharset(ctype: String) = {
    var charset = DEFAULT_CHARSET
    if (!StringUtils.isEmpty(ctype)) {
      val params = ctype.split(";")
      breakable {
        for (param <- params) {
          var param_copy = param.trim
          if (param_copy.startsWith("charset")) {
            val pair = param_copy.split("=", 2)
            if (pair.length == 2)
              if (!StringUtils.isEmpty(pair(1)))
                charset = pair(1).trim
            break()
          }
        }
      }
    }
    charset
  }

  def checkHttpResponseError(httpResponse: SimpleHttpResponse): Unit = {
    var content = httpResponse.getContent
    if (StringUtils.isNotEmpty(content)) {

      if (!httpResponse.isSuccess ||
        (StringUtils.containsIgnoreCase(content,"errors") && StringUtils.containsIgnoreCase(content, "errorType"))) {
        try {
          val errorDetail = mapper.fromJson(content, classOf[ErrorDetail])
          httpResponse.setErrorDetail(errorDetail)
          logger.error("http failed: " + errorDetail.toString)
        } catch {
          case e: Throwable => logger.error("http failed with exception: [" + e + "]" + content)
        }
      }
    }
  }

  def pushQueriesForDataFrame(builder: QueryBuilder): ArrayBuffer[String] = {
    val dataArray: ArrayBuffer[String] = new ArrayBuffer[String]

    for (query <- builder.buildOneByOne) {
      val response: HttpResponse = httpClient.doPost(this.serviceUrl, query)
      val entity = response.getEntity
      if (entity != null) {
        val charset = getResponseCharset(if (entity.getContentType == null) "" else entity.getContentType.getValue)
        val content = EntityUtils.toString(entity, charset)
        dataArray.append(content)
      } else {
        logger.error(s"request data failed, status code: $response.getStatusLine.getStatusCode")
      }
    }
    dataArray
  }

  override def pushQueries(builder: QueryBuilder): SimpleHttpResponse = {
    val response: HttpResponse = httpClient.doPost(this.serviceUrl, builder.build)

    val simpleResponse = new SimpleHttpResponse
    simpleResponse.setStatusCode(response.getStatusLine.getStatusCode)

    val entity = response.getEntity
    println(response.getStatusLine)
    if (entity != null) {
      val charset = getResponseCharset(if (entity.getContentType == null) "" else entity.getContentType.getValue)
      val content = EntityUtils.toString(entity, charset)
      simpleResponse.setContent(content)
    }
    checkHttpResponseError(simpleResponse)
    simpleResponse
  }
}
