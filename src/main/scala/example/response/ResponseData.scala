package example.response

import java.util.HashMap // scala.collection.mutable.HashMap convert empty data

case class ModelList() {
  var fieldList: Array[HashMap[String, Any]] = new Array[HashMap[String, Any]](2)
  var inputs: Array[String] = null
  var targets: Array[String] = null
  var numOfFields: Int = 0
}

case class ResponseData() {
  private var dataModel: ModelList = null
  private var data: HashMap[String, Array[Number]] = null

  def getDataModel: ModelList = this.dataModel

  def setDataModel(dataModel: ModelList): Unit = {
    this.dataModel = dataModel
  }

  def getData: HashMap[String, Array[Number]] = this.data

  def setData(data: HashMap[String, Array[Number]]): Unit = {
    this.data = data
  }
}
