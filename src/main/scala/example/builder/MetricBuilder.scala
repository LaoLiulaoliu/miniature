package example.builder

import java.io.IOException
import java.util.{ArrayList, List}
import com.google.gson.{Gson, GsonBuilder}

class MetricBuilder {
  private val metrics = new ArrayList[Metric]
  private var mapper: Gson = null

  val builder = new GsonBuilder
  mapper = builder.create

  def getInstance: MetricBuilder = {
    new MetricBuilder
  }

  /**
   * Adds a metric to the builder.
   *
   * @param metricName
   * metric name
   * @return the new metric
   */
  def addMetric(metricName: String): Metric = {
    val metric = new Metric(metricName)
    metrics.add(metric)
    metric
  }

  /**
   * Returns a list of metrics added to the builder.
   *
   * @return list of metrics
   */
  def getMetrics: List[Metric] = metrics

  /**
   * Returns the JSON string built by the builder. This is the JSON that can
   * be used by the client add metrics.
   *
   * @return JSON
   * @throws IOException
   * if metrics cannot be converted to JSON
   */
  @throws[IOException]
  def build: String = {
    mapper.toJson(metrics)
  }
}
