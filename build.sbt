name := "jr-microservice-workshop-test"

organization := "com.gu"

description:= "Build an API microservice with API gateway and lambda"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-dead-code"
)


libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)

enablePlugins(JavaAppPackaging, RiffRaffArtifact)

topLevelDirectory in Universal := None
packageName in Universal := normalizedName.value

riffRaffPackageType := (packageBin in Universal).value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"${name.value}-cfn/cfn.yaml")
