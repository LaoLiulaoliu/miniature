##### requirements:

    java:  11.0.9.1
    sbt:   1.4.6
    scala: 2.12.12
    spark: 3.0.1-bin-hadoop3.2


### error for jetty
[error] /mnt/d/git/common/src/main/scala/example/JettyLauncher.scala:17:13: overloaded method value addServlet with alternatives:
[error]   (x$1: org.eclipse.jetty.servlet.ServletHolder,x$2: String)Unit <and>
[error]   (x$1: Class[_ <: jakarta.servlet.Servlet],x$2: String)org.eclipse.jetty.servlet.ServletHolder <and>
[error]   (x$1: String,x$2: String)org.eclipse.jetty.servlet.ServletHolder
[error]  cannot be applied to (Class[example.OperatorServlet], String)
  [error]     context.addServlet(classOf[OperatorServlet], "/*")
  [error]
