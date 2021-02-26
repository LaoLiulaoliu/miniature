package example.http

import java.io.IOException

import example.request.QueryBuilder
import example.response.SimpleHttpResponse

trait HttpClient {
  val BATCH_QUERY_POST_API = "/api/queryengine/batch_query"
  val DEFAULT_CHARSET = "UTF-8"

  @throws[IOException]
  def pushQueries(builder: QueryBuilder): SimpleHttpResponse
}
