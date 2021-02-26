package example.request

import java.io.IOException
import java.util.ArrayList

import com.google.gson.Gson


class QueryBuilder {
  private var queries: ArrayList[Query] = new ArrayList[Query]
  private val mapper: Gson = new Gson

  def getQueries: ArrayList[Query] = this.queries

  def setQueries(queries: ArrayList[Query]): Unit = {
    this.queries = queries
  }

  def addQuery(query: Query): Unit = {
    this.queries.add(query)
  }

  def clearQuery(): Unit = {
    this.queries.clear()
  }

  def buildOneByOne: Array[String] = {
    for (q <- queries.toArray) yield "[" + mapper.toJson(q) + "]"
  }

  @throws[IOException]
  def build: String = { // verify that there is at least one tag for each metric
    //    checkState(query.arguments.getStart > 0, " must contain start.")
    //    checkState(query.arguments.getQueries != null, " must contain at least one subQuery.")
    mapper.toJson(queries)
  }
}
