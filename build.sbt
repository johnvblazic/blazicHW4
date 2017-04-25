name := "HW4"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.clulab" %% "processors-main" % "6.0.1",
  "org.clulab" %% "processors-corenlp" % "6.0.1",
  "org.clulab" %% "processors-models" % "6.0.1"
)

val buildSettings = Defaults.defaultSettings ++ Seq(
  //…
  javaOptions += "-Xmx8G"
  //…
)