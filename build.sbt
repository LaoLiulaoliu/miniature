name := "common"

version := "0.1"

scalaVersion := "2.12.13"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalanlp" %% "breeze" % "1.1",
  "org.scalanlp" %% "breeze-natives" % "1.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.14.0",
  "org.apache.commons" % "commons-lang3" % "3.11",
  "org.apache.httpcomponents" % "httpclient" % "4.5.13",
  "com.google.guava" % "guava" % "30.1-jre",
  "com.google.code.gson" % "gson" % "2.8.6",
  "org.apache.spark" %% "spark-core" % "3.0.1",
  "org.apache.spark" %% "spark-sql" % "3.0.1",
  "org.scalatra" %% "scalatra" % "2.7.+",
  "com.alibaba" % "fastjson" % "1.2.75",
  "org.eclipse.jetty" % "jetty-webapp" % "11.0.1",
  "ch.qos.logback" % "logback-classic" % "1.1.5",
  "javax.servlet"  %  "javax.servlet-api" % "3.1.0"
)

enablePlugins(JettyPlugin)