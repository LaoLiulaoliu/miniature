package example.builder

import java.util.{Map, HashMap}
import com.google.gson.annotations.SerializedName
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkArgument
import example.utils.Preconditions

class Metric {
  @SerializedName("metric")
  private var name: String = null

  private var timestamp = 0L

  private var value: Any = null

  var tags:Map[String, String] = new HashMap[String, String] // mutable

  def this(name: String) {
    this()
    this.name = Preconditions.checkNotNullOrEmpty(name)
  }

  /**
   * Adds a tag to the data point.
   *
   * @param name
   * tag identifier
   * @param value
   * tag value
   * @return the metric the tag was added to
   */
  def addTag(name: String, value: String): Metric = {
    Preconditions.checkNotNullOrEmpty(name)
    Preconditions.checkNotNullOrEmpty(value)
    tags.put(name, value)
    this
  }

  /**
   * Adds tags to the data point.
   *
   * @param tags
   * map of tags
   * @return the metric the tags were added to
   */
  def addTags(tags: Map[String, String]): Metric = {
    checkNotNull(tags)
    tags.putAll(tags)
    this
  }

  /**
   * set the data point for the metric.
   *
   * @param timestamp
   * when the measurement occurred
   * @param value
   * the measurement value
   * @return the metric
   */
  protected def innerAddDataPoint(timestamp: Long, value: Any): Metric = {
    checkArgument(timestamp > 0)
    this.timestamp = timestamp
    this.value = checkNotNull(value)
    this
  }

  /**
   * Adds the data point to the metric with a timestamp of now.
   *
   * @param value
   * the measurement value
   * @return the metric
   */
  def setDataPoint(value: Long): Metric = innerAddDataPoint(System.currentTimeMillis, value)

  def setDataPoint(timestamp: Long, value: Long): Metric = innerAddDataPoint(timestamp, value)

  /**
   * Adds the data point to the metric.
   *
   * @param timestamp
   * when the measurement occurred
   * @param value
   * the measurement value
   * @return the metric
   */
  def setDataPoint(timestamp: Long, value: Double): Metric = innerAddDataPoint(timestamp, value)

  /**
   * Adds the data point to the metric with a timestamp of now.
   *
   * @param value
   * the measurement value
   * @return the metric
   */
  def setDataPoint(value: Double): Metric = innerAddDataPoint(System.currentTimeMillis, value)

  /**
   * Time when the data point was measured.
   *
   * @return time when the data point was measured
   */
  def getTimestamp: Long = timestamp

  def getValue: Any = value

  def stringValue: String = value.toString

  def longValue: Long = value.asInstanceOf[Number].longValue

  //  catch {
  //    case e: Exception =>
  //      throw new IOException("Value is not a double")
  ////      throw new DataFormatException("Value is not a long")
  //  }

  def doubleValue: Double = value.asInstanceOf[Number].doubleValue

  //  catch {
  //    case e: Exception =>
  //      throw new IOException("Value is not a double")
  ////      throw new DataFormatException("Value is not a double")
  //  }

  def isDoubleValue: Boolean = !((value.asInstanceOf[Number]).doubleValue == Math.floor((value.asInstanceOf[Number]).doubleValue))

  def isIntegerValue: Boolean = value.asInstanceOf[Number].doubleValue == Math.floor(value.asInstanceOf[Number].doubleValue)

  /**
   * Returns the metric name.
   *
   * @return metric name
   */
  def getName: String = name

  /**
   * Returns the tags associated with the data point.
   *
   * @return tag for the data point
   */
  def getTags = tags
}
