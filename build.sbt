name := "jr-microservice-workshop"

organization := "com.gu"

description:= "Get the value of acquisitions"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-dead-code"
)

val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.gu" %% "scanamo" % "0.9.2",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.akka" %% "akka-http" % "10.0.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)

enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"${name.value}-cfn/cfn.yaml")
