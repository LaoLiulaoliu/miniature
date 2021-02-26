package example

import com.google.gson.Gson
import example.response.ResponseData
import org.apache.spark.ml.feature.PolynomialExpansion
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object test {

  def localKm(): Unit = LocalKmeans().run()

  def maptuple(): Unit = {
    var data: mutable.Map[Long, Array[Float]] = mutable.Map[Long, Array[Float]]()
    data += (1L -> Array(7.1F, Float.NaN))
    data += (2L -> Array(Float.NaN, 4.3F))

    if (data.contains(1L)) {
      data(1)(1) = 5F
    }
    data.foreach(x => println(x._2.toSeq))
  }

  def twomap(): Unit = {
    var data: ArrayBuffer[mutable.Map[Long, Float]] = new ArrayBuffer[mutable.Map[Long, Float]]
    val columns = Seq("outdoor_temperature", "instantaneous_heat")

    var json = "[{\"dataModel\":{\"fieldList\":[{\"name\":\"time\",\"dataType\":\"LONG\",\"metaData\":{},\"storage\":\"LONG\"},{\"name\":\"outdoor_temperature\",\"dataType\":\"DOUBLE\",\"metaData\":{},\"storage\":\"DOUBLE\"}],\"inputs\":[],\"numOfFields\":2,\"targets\":[]},\"data\":{\"time\":[1611871200000,1611872100000,1611873000000,1611873900000],\"outdoor_temperature\":[-9.0,-8.0,-8.1,-8.0]}}]"

    var gson = new Gson
    var jsonMap = gson.fromJson(json.substring(1, json.length - 1), classOf[ResponseData]).getData

    var time: List[Number] = null
    var value: List[Number] = null
    jsonMap.entrySet().forEach(x => {
      if (x.getKey == "time")
        time = x.getValue.toList
      else
        value = x.getValue.toList
    })

    var record: mutable.Map[Number, Number] = mutable.Map()
    time.zip(value).map {
      case (x, y) => record += (x -> y)
    }
    println(record)
  }

  def poly(spark: SparkSession): Unit = {
    val data = Array(
      Vectors.dense(2.0, 1.0),
      Vectors.dense(4.0, 2.0),
      Vectors.dense(3.0, -1.0)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    val polyExpansion = new PolynomialExpansion()
      .setInputCol("features")
      .setOutputCol("polyFeatures")
      .setDegree(2)

    val polyDF = polyExpansion.transform(df)

    polyDF.show(false)
  }

  def conn_hdfs(spark: SparkSession): Unit = {
    val files = "hdfs://bigdata-master001:8020/flume/3a13291f7806000/2021-02-22/flume-0.flume-headless.pre-release.svc.cluster.local__data_iot_362259ac880c000_SpeedUp_flume.1613923405613.gz"
    val columns = Seq("timestamp", "temperature", "humidity", "type", "corpId", "uid")

    val df = spark.read.json(files).filter("type = 'HumiditySensor'").select("timestamp", "temperature", "humidity", "type", "corpId", "uid")
    df.show()
  }

  def main(args:Array[String]): Unit = {
//    localKm()
//    twomap()
    val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("Heatload")
      .getOrCreate()

    conn_hdfs(spark)

    spark.stop()
  }
}
