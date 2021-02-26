package example.http

import java.io.IOException
import java.util.concurrent.TimeUnit

import com.google.gson.JsonParser
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.{HttpPost, HttpUriRequest}
import org.apache.http.conn.{ConnectionKeepAliveStrategy, HttpClientConnectionManager}
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeaderElementIterator
import org.apache.http.protocol.{HTTP, HttpContext}

import scala.util.control.Breaks.{break, breakable}

class PoolingHttpClient {
  val DEFAULT_MAX_TOTAL_CONNECTIONS = 200

  val DEFAULT_MAX_CONNECTIONS_PER_ROUTE = DEFAULT_MAX_TOTAL_CONNECTIONS

  val DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS = 10 * 1000
  val DEFAULT_READ_TIMEOUT_MILLISECONDS = 10 * 1000
  val DEFAULT_WAIT_TIMEOUT_MILLISECONDS = 10 * 1000

  val DEFAULT_KEEP_ALIVE_MILLISECONDS = 5 * 60 * 1000

  val DEFAULT_RETRY_COUNT = 2

  var keepAlive = DEFAULT_KEEP_ALIVE_MILLISECONDS

  var maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS
  var maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE

  var connectTimeout = DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS
  var readTimeout = DEFAULT_READ_TIMEOUT_MILLISECONDS
  var waitTimeout = DEFAULT_WAIT_TIMEOUT_MILLISECONDS

  var retries = DEFAULT_RETRY_COUNT
  var connManager = new PoolingHttpClientConnectionManager
  var httpClient: CloseableHttpClient = null

  val keepAliveStrategy = new ConnectionKeepAliveStrategy() {
    override def getKeepAliveDuration(response: HttpResponse, context: HttpContext): Long = {
      val it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE))
      while (it.hasNext) {
        val he = it.nextElement
        val param = he.getName
        val value = he.getValue
        if (value != null && param.equalsIgnoreCase("timeout"))
          return value.toLong * 1000
      }
      keepAlive
    }
  }

  connManager.setMaxTotal(maxTotalConnections)
  // Increase default max connection per route
  connManager.setDefaultMaxPerRoute(maxConnectionsPerRoute)

  // config timeout
  val config: RequestConfig = RequestConfig.custom.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(waitTimeout).setSocketTimeout(readTimeout).build

  httpClient = HttpClients.custom.setKeepAliveStrategy(keepAliveStrategy).setConnectionManager(connManager).setDefaultRequestConfig(config).build

  // detect idle and expired connections and close them
  val staleMonitor = new IdleConnectionMonitorThread(connManager)
  staleMonitor.start()

  @deprecated
  def parseHeader(post: HttpPost, header: String = ""): Unit = {
    val obj = new JsonParser().parse(header).getAsJsonObject
    obj.entrySet().forEach(x => post.addHeader(x.getKey, x.getValue.toString))
  }

  @throws[IOException]
  def doPost(url: String, data: String, header: String = ""): HttpResponse = {
    val postMethod = new HttpPost(url)
    postMethod.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON))
    //println(postMethod.getEntity)
    execute(postMethod)
  }

  @throws[IOException]
  def execute(request: HttpUriRequest): HttpResponse = {
    var response: HttpResponse = null
    var tries = retries + 1

    breakable {
      while (true) {
        tries -= 1
        try {
          response = httpClient.execute(request)
          break()
        } catch {
          case e: IOException =>
            if (tries < 1) throw e
        }
      }
    }
    response
  }

  @throws[IOException]
  def shutdown(): Unit = {
    httpClient.close()
  }

}

class IdleConnectionMonitorThread extends Thread {
  var connMgr: HttpClientConnectionManager = null
  private var idelFlag = true

  def this(connMgr: HttpClientConnectionManager) {
    this()
    this.connMgr = connMgr
  }

  override def run(): Unit = {
    try
      while (idelFlag == true) {
        this synchronized wait(5000)
        connMgr.closeExpiredConnections()
        connMgr.closeIdleConnections(60, TimeUnit.SECONDS)
      }
    catch {
      case ex: InterruptedException =>
        shutdown()

    }
  }

  def shutdown(): Unit = {
    idelFlag = false
    this.synchronized {
      notifyAll()
    }
  }

}
