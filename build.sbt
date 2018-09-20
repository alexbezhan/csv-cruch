name := "automobile-map"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVer = "2.5.16"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVer

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaVer

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVer

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % akkaVer

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.5"