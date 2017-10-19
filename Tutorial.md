# jr-api-gateway-workshop


1. Go to https://repo-genesis.herokuapp.com/ and create a repo.

2. Set up the repo locally and make your first commit

3. Add a gitignore file to the root (see https://github.com/guardian/jr-api-gateway-workshop/blob/master/.gitignore)

4. Create a file called build.sbt in the root with the following: (an example name of project is jr-microservice workshop, you will want to call it something unique so use your initials)

```
name := "<name of project>"

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

val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"
)

enablePlugins(JavaAppPackaging, RiffRaffArtifact)

topLevelDirectory in Universal := None
packageName in Universal := normalizedName.value

riffRaffPackageType := (packageBin in Universal).value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"${name.value}-cfn/cfn.yaml")
```




5. Create a file called plugins.sbt in root/project

6. Add the following 2 lines : 
`addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0")
	addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.0.0")`

7. Compile using `sbt compile`

8. Create new Scala class Lambda.scala in src/main/scala/com/gu/microserviceWorkshop with the following: 

```
package com.gu.microserviceWorkshop

object Lambda {

  def handler(): String = {
    "hello world"
  }

} 
```
The ‘handler’ function is the function that will be called when your lambda is invoked. You can name it whatever you want, but handler will do!  

9. Push to github, then go to https://teamcity.gutools.co.uk/admin/editProject.html?projectId=Playground

10. Create subproject, give it a unique name

11. Add a 'simple build tool (scala)' build step "

12. Go to “Edit build configuration”

13. Edit “sbt command” to: clean compile test riffRaffUpload

14. Go to VCS roots, choose the root

15. Show advanced options

16. Set branch specifications to +:refs/heads/* (this allows all your branches from github to automatically build)

17. Check that branches are building by pushing to a new branch

18. Create a riff-raff.yaml and add the following: 

```
stacks: [playground]
regions: [eu-west-1]
templates:
  <name of project>:
    type: aws-lambda
    app: <name of project>
    contentDirectory:   <name of project>
    parameters:
      bucket: gu-jr-microservice-workshop-dist
      functionNames: [<name of project>-]
      fileName:  <name of project>.zip
      prefixStack: false
      
  deployments:
    <name of project>-upload:
      template: <name of project>
      actions: [uploadLambda]
      ```

18. Push, wait for build on TC, go to https://riffraff.gutools.co.uk/deployment/request and start typing in your project name. Click deploy
