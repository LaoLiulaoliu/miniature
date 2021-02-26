package example

import org.apache.spark.ml.feature.{PolynomialExpansion, VectorAssembler}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.types.{FloatType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

import scala.collection.mutable.ListBuffer

object UtilForRdd extends Serializable {
  val round1 = (x: Float) => {
    math.round(x * 10F) / 10F
  }

  val computePercentile = (data: Iterable[Any], tile: Float) => {
    var dataList: ListBuffer[Float] = ListBuffer()
    data.foreach(x => dataList.append(x.asInstanceOf[Float]))

    if (tile < 0 || tile > 100 || data.isEmpty)
      0
    else {
      dataList = dataList.sorted
      val cnt = dataList.length
      if (cnt == 1) dataList(0)
      else {
        val n = (tile / 100F) * cnt
        val k = math.floor(n).toInt
        if (k == cnt) {
          dataList(cnt - 1)
        } else {
          dataList(k) + (n - k) * (dataList(k + 1) - dataList(k))
        }
      }
    }
  }
}

class SparkRunner() {

  val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("Heatload")
    .getOrCreate()
  import spark.implicits._

  val columns = Seq("outdoor_temperature", "instantaneous_heat")
  val schema: StructType = StructType(Array(
    StructField("outdoor_temperature", FloatType, nullable = true),
    StructField("instantaneous_heat", FloatType, nullable = true)
  ))

  def getDF: DataFrame = {
    val data = new AccessTSDB().getDataFromDSDB
    val seqRow: Seq[Row] = data.map(line => Row(line._1, line._2))
    val rdd = spark.sparkContext.parallelize(seqRow)

    rdd.map(x => (UtilForRdd.round1(x(0).asInstanceOf[Float]), x(1)))
      .groupByKey()
      .map(t => (t._1, UtilForRdd.computePercentile(t._2, 60)))
      .toDF(columns: _*)
  }

  def runLR(): Unit = {
    val newDF = getDF
    newDF.show()

    val assembler = new VectorAssembler().setInputCols(Array("outdoor_temperature")).setOutputCol("features")
    val datasets = assembler.transform(newDF)

    val lr = new LinearRegression()
      .setFeaturesCol("features").setLabelCol("instantaneous_heat")
      .setFitIntercept(true).setStandardization(true).setMaxIter(100)
//      .setRegParam(0.3).setElasticNetParam(1) no regularization has same result with sklearn


    val model = lr.fit(datasets)
    model.extractParamMap()
    println(s"Coefficients: ${model.coefficients} Intercept: ${model.intercept}")

    val trainingSummary = model.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: ${trainingSummary.objectiveHistory.toList}")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
  }

  def runPipeline(): Unit = {
    val newDF = getDF
    val df = newDF.randomSplit(Array(0.7, 0.3))

    val assembler = new VectorAssembler().setInputCols(Array("outdoor_temperature")).setOutputCol("features")
    val polyExpansion = new PolynomialExpansion()  // interaction_only=False, include_bias=False
      .setInputCol("features")
      .setOutputCol("polyFeatures")
      .setDegree(2)
    val lr = new LinearRegression()
      .setFeaturesCol("polyFeatures").setLabelCol("instantaneous_heat")
      .setFitIntercept(true).setStandardization(true).setMaxIter(100)

    val pipeline = new Pipeline().setStages(Array(assembler, polyExpansion, lr))
    val model = pipeline.fit(df(0))

    model.write.overwrite().save("/tmp/spark-linear-regression-model")
    val sameModel = PipelineModel.load("/tmp/spark-linear-regression-model")
    val prediction = sameModel.transform(df(1))
    prediction.select("features", "polyFeatures", "instantaneous_heat", "prediction").show()

    spark.stop()
  }

  def toCSV(): Unit = {
    val df = getDF
    df.coalesce(1).write.mode(SaveMode.Overwrite).option("header","true").option("sep",",").csv("heatload.csv")
  }
}
