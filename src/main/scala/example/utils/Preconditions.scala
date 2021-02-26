package example.utils

import com.google.common.base.Preconditions.checkNotNull
import scala.util.parsing.json.JSON
import scala.collection.immutable.Map

object Preconditions {
  def checkNotNullOrEmpty(reference: String): String = {
    checkNotNull(reference)
    if (reference.isEmpty)
      throw new IllegalArgumentException
    reference
  }

  def isGoodJson(json: String):Boolean = {
    if (null == json) {
      return false
    }
    val result =  JSON.parseFull(json) match {
      case Some(_:  Map[String, Any]) => true
      case None => false
      case _ => false
    }
    result
  }
}
