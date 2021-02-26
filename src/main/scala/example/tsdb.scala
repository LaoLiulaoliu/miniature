//package example
//
//import org.apache.spark.sql.SparkSession
//
//case class tsdb() {
//  override def compute(sparkSession: SparkSession): Dataset[Row] = {
//    val build = QueryBuild.getInstance();
//
//    val querie = new QueryDataQuerie();
//
//    querie.setMetric(metrics)
//
//    //设置聚合形式
//    querie.setAggregator(aggr);
//
//    //按tag查询的条件
//    if(null != tags && tags.indexOf(":") != -1){
//      val tag = tags.split(",");
//      for(t <- tag){
//        val kv = t.split(":")
//        querie.addFilter(tagvFilterRule, kv(0), kv(1), true);
//      }
//    }
//
//    //设置时间粒度
//    if(!downsample.trim.isEmpty){
//      querie.setDownsample(downsample+"-"+querie.aggregator);
//    }
//    //设置开始时间和结束时间
//    build.setDataReq(start,end).addQuerie(querie);
//    val clientImpl = new HttpClientImpl("http://"+url);
//    val qsdr = clientImpl.get(build);
//    val jsonparser:JsonParser = new JsonParser();
//    val contentqsdr:JsonElement = jsonparser.parse(qsdr)
//    val c:JsonElement = jsonparser.parse(contentqsdr.getAsJsonObject.get("content").getAsString)
//    var list = new ArrayList[String]
//    var metric=""
//    var tagsjson=""
//    if(c.getAsJsonArray.size()==0){
//      return null;
//    }else{
//      for (i <- 0 to c.getAsJsonArray.size()-1){
//        val dps:JsonElement = c.getAsJsonArray.get(i).getAsJsonObject.get("dps")
//        metric = c.getAsJsonArray.get(i).getAsJsonObject.get("metric").getAsString
//        tagsjson = c.getAsJsonArray.get(i).getAsJsonObject.get("tags").toString()
//        val set = dps.getAsJsonObject.entrySet()
//        val ite = set.iterator();
//
//        while(ite.hasNext()){
//          val en = ite.next();
//          list.add(tagsjson+";"+en.getKey+";"+en.getValue.getAsString)
//        }
//      }
//    }
//
//
//
//    val rdd = sparkSession.sparkContext.parallelize(list.toArray(), 3)
//    val rowRdd = rdd.map(x => {
//      val kv = x.toString().split(";");
//      RowFactory.create(metric,kv(0),kv(1),kv(2))
//    });
//    val schemaString = "metric tags timestamp value"
//    val schema = StructType(schemaString.split(" ").map(a => StructField(a, StringType, true)))
//    val df = sparkSession.createDataFrame(rowRdd, schema)
//    df.show()
//    return df
//  }
//}
