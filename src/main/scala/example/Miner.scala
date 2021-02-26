package scala.example

import example.SparkRunner

object Miner {
  def main(args:Array[String]): Unit = {
    new SparkRunner().runPipeline()
  }
}
