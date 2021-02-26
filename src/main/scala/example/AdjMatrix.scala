package example

import java.io.File
import java.util.Scanner

import scala.collection.mutable.ArrayBuffer

class AdjMatrix {
  var V = 0 // 顶点
  var E = 0 // 边
  var adj: Array[Array[Int]] = _ // 二维数组

  def createAdjMatrix(filename: String): Unit = {
    val file = new File(filename)

    val scanner = new Scanner(file)
    V = scanner.nextInt()
    adj = Array.ofDim[Int](V, V)
    E = scanner.nextInt()
    for (i <- 0 until E) {
      val a = scanner.nextInt()
      val b = scanner.nextInt()
      adj(a)(b) = 1;
      adj(b)(a) = 1
    }
  }

  override def toString: String = {
    val stringBuffer = new StringBuffer()
    stringBuffer.append(String.format(s"V = ${V}, E= ${E}\n"))
    for (i <- 0 until V) {
      for (j <- 0 until V) {
        stringBuffer.append("\t").append(adj(i)(j))
      }
      stringBuffer.append("\n")
    }
    stringBuffer.toString()
  }

  /**
   * 获得 图中顶点的个数
   *
   * @return
   */
  def getV() = {
    V
  }

  /**
   * 获得 图中边的个数
   *
   * @return
   */
  def getE() = {
    E
  }

  /**
   * 两个顶点间是否有边
   *
   * @param v
   * @param w
   * @return
   */
  def hasEdge(v: Int, w: Int) = {
    adj(v)(w) == 1
  }

  /**
   * 获取v的所有邻边
   *
   * @param v
   * @return
   */
  def getAdjacentSide(v: Int) = {
    var arr = ArrayBuffer[Int]()
    for (i <- 0 until V) {
      if (this.adj(v)(i) == 1) {
        arr += i
      }
    }
    arr.toList
  }
}
