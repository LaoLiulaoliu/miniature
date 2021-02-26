package example

import com.google.gson.Gson
import example.http.HttpClientImpl
import example.request.{Query, QueryBuilder}
import example.response.ResponseData
import org.apache.commons.lang3.StringUtils

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class AccessTSDB {
  val host = ""
  val token = ""
  val client = new HttpClientImpl
  client.HttpClientImpl(host, token)

  def demoQuery(): Unit = {
    val queryBuilder = new QueryBuilder
    var sql = "select downsample(10000),avg(temperature) as outdoor_temperature from Weather where uid='fd198350-6349-3d43-b6b9-fc7bd2d6e8f9'"
    queryBuilder.addQuery(new Query(sql, 1611266866868L, 1611270466867L))
    queryBuilder.addQuery(new Query(sql, 1611266866868L, 1611270466867L))
    val response = client.pushQueries(queryBuilder)
    println(response.getContent)
  }

  def create_batch_queries(sqls: Array[String], start: Long, end: Long): ListBuffer[Query] = {
    val queries: ListBuffer[Query] = new ListBuffer[Query]
    val interval = 3600000L
    var tmp: Long = 0L
    var begin = start

    do {
      tmp = if (begin + interval < end) begin + interval - 1 else end
      for (sql <- sqls)
        queries.append(new Query(sql, begin, tmp))
      begin += interval
    } while (begin < end)
    queries
  }

  def getDataFromDSDB: Seq[(Float, Float)] = {
    val queryBuilder = new QueryBuilder
    queryBuilder.clearQuery()
    val sqls: Array[String] = Array(
      "select downsample(10000),avg(temperature) as outdoor_temperature from Weather where uid='fd198350-6349-3d43-b6b9-fc7bd2d6e8f9'",
      "select downsample(10000),avg(instantaneousHeatLine1) as instantaneous_heat from HeatExchangeStation where uid='bc3e60ed-fcef-4d7e-8ea4-a1afcc2d8a4b'"
    )
    val queries: ListBuffer[Query] = create_batch_queries(sqls, 1611647563000L, 1612165963000L)
    queries.foreach(q => queryBuilder.addQuery(q))
    var dataArray: ArrayBuffer[String] = client.pushQueriesForDataFrame(queryBuilder)
    parseData(dataArray)
  }

  def parseData(dataArray: ArrayBuffer[String]): Seq[(Float, Float)] = {
    val columns = Seq("outdoor_temperature", "instantaneous_heat")
    var temperatureRecord: mutable.Map[Long, Number] = mutable.Map()
    var heatRecord: mutable.Map[Long, Number] = mutable.Map()

    var gson = new Gson
    dataArray.foreach(line => {
      var time: List[Number] = null
      var value: List[Number] = null
      var valueName: String = null

      var jsonMap = gson.fromJson(line.substring(1, line.length - 1), classOf[ResponseData]).getData
      jsonMap.entrySet().forEach(x => {
        if (x.getKey == "time")
          time = x.getValue.toList
        else {
          valueName = x.getKey
          value = x.getValue.toList
        }
      })

      if (StringUtils.equalsAnyIgnoreCase(valueName, columns.head)) {
        time.zip(value).map {
          case (x, y) => temperatureRecord += (x.longValue() -> y)
        }
      } else if (StringUtils.equalsAnyIgnoreCase(valueName, columns(1))) {
        time.zip(value).map {
          case (x, y) => heatRecord += (x.longValue() -> y)
        }
      }
    })

    val timeSet = (temperatureRecord.keySet & heatRecord.keySet).toSeq
    timeSet.map(x => {
      (temperatureRecord.getOrElse(x, Float.NaN).asInstanceOf[Number].floatValue(),
        heatRecord.getOrElse(x, Float.NaN).asInstanceOf[Number].floatValue())
    })
  }

}
