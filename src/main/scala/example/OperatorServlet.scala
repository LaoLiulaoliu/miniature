package example

import org.scalatra.ScalatraServlet
import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}

class OperatorServlet extends ScalatraServlet {
  post("/run") {
    val jsonString = request.body
    val js: JSONObject = JSON.parseObject(jsonString)
    js.get("who")
  }

  post("/predict") {
    val jsonString = request.body
  }

  get("/") {
    "bingo"
  }
}
