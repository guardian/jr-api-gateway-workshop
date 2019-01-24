# jr-api-gateway-workshop


1. Go to https://repo-genesis.herokuapp.com/ and create a repo with what will now be referrerd to as `<name of project>`.

2. Set up the repo locally and make your first commit (if you visit your repo on github there will be instructions on how to do this on the Code tab) NOTE: you will want to create a new folder for this


3. Add a gitignore file to the root (you can paste the contend from this one https://github.com/guardian/jr-api-gateway-workshop/blob/master/.gitignore)

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

Make sure you replace `<name of project>` with your project name you decided on in step 1



5. Create a file called plugins.sbt in root/project

6. Add the following 2 lines :
```
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0")
addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.0.0")
```

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

10. Click Create subproject and select "Pointing to Github.com repository"

11. Find your repository and select it

12. Give the project a unique Project name

13. Once selected, a screen called "Auto-detected Build Steps" will appear. Click on the link in the small text below called "configure build steps manually"

14. Select Simple Build Tool (scala) as the Runner Type

15. On the "New Build Step" page that comes up, edit “Sbt commands” to: `clean compile test riffRaffUpload` and click save

16. Go to Version Control Settings in the side menu and click edit on the VCS root that appears

17. Show advanced options

18. Set branch specifications to +:refs/heads/* (this allows all your branches from github to automatically build) and save

19. Check that branches are building by pushing to a new branch and navigating to your build through the Projects in the toolbar (your project will be under Playground)

20. Create a riff-raff.yaml and add the following:

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

Make sure you replace <name of project> with your project name you decided on in step 1

21. Push, wait for build on TC, go to https://riffraff.gutools.co.uk/deployment/request and start typing in your project name. Click deploy.
 This will upload your artifact to https://s3.console.aws.amazon.com/s3/buckets/gu-jr-microservice-workshop-dist/playground/PROD/<your project name>/<your project name>.zip

22. Create a cloudformation template in the root, cfn.yaml, with the following:

```
AWSTemplateFormatVersion: 2010-09-09
Description: Get

Parameters:
  Stack:
    Description: Stack name
    Type: String
    Default: playground
  App:
    Description: Application name
    Type: String
    Default: <name of your project>
  Stage:
    Description: Stage name
    Type: String
    AllowedValues:
      - CODE
      - PROD
    Default: PROD
  DeployBucket:
    Description: Bucket where RiffRaff uploads artifacts on deploy
    Type: String
    Default: gu-jr-microservice-workshop-dist

Resources:
  ExecutionRole:
    Type: AWS::IAM::Role
    Properties:
      AssumeRolePolicyDocument:
        Statement:
          - Effect: Allow
            Principal:
              Service:
                - lambda.amazonaws.com
            Action: sts:AssumeRole
      Path: /
      Policies:
        - PolicyName: logs
          PolicyDocument:
            Statement:
              Effect: Allow
              Action:
                - logs:CreateLogGroup
                - logs:CreateLogStream
                - logs:PutLogEvents
              Resource: arn:aws:logs:*:*:*
        - PolicyName: lambda
          PolicyDocument:
            Statement:
              Effect: Allow
              Action:
                -  lambda:InvokeFunction
              Resource: "*"

  WorkshopLambda:
    Type: AWS::Lambda::Function
    Properties:
      FunctionName: !Sub ${App}-${Stage}
      Code:
        S3Bucket:
          Ref: DeployBucket
        S3Key:
          !Sub ${Stack}/${Stage}/${App}/${App}.zip
      Environment:
        Variables:
          Stage: !Ref Stage
          Stack: !Ref Stack
          App: !Ref App
      Description: Test lambda for workshop
      Handler: com.gu.microserviceWorkshop.Lambda::handler
      MemorySize: 512
      Role: !GetAtt ExecutionRole.Arn
      Runtime: java8
      Timeout: 300

```

Don't forget to replace `<name of your project>`.
Here we have defined 2 resources: An execution roll that is given permission to invoke the Lambda,
and the Lambda itself. You will see that under 'Code', we have told the template where to find our
code, in the bucket defined in the DeployBucket parameter at the top of the template (we use Ref to refer to a single parameter), in this case gu-jr-microservice-workshop-dist,
and the key (which is basically the path to the file and the filename).which in our case will be playground/PROD/<name of your project>/<name of your project>.zip.
!Sub is used mcuh like string interpolation to inject the parameters section defined at the top in one go into strings.

The LambdaInvokePermission

23. Add the following deployments to your riff-raff.yaml (same indentation level as the uploadLambda deployment you added before)
```
    <name of your project>-cfn:
      type: cloud-formation
      app: <name of your project>
      dependencies: [<name of your project>-upload]
      parameters:
        prependStackToCloudFormationStackName: false
        cloudFormationStackName: <name of your project>
        templatePath: cfn.yaml

    <name of your project>-lambda-update:
      template: <name of your project>
      actions: [updateLambda]
      dependencies: [<name of your project>-cfn]
```

The first one is of type 'cloudformation', and what this does is tells riff-raff to upload or update
the cloudformation stack with the Stack, Stage and App specified in the parameters section of the cloudformation
template.

The second one, which has the action updateLambda, updates the lambda with the .zip file that is in your S3 bucket.

Push, wait for teamcity to build your branch then try to deploy on riff-raff.

If you are having trouble knowing if your riff-raff template is formatted correctly, try going to https://riffraff.gutools.co.uk/
, then navigating to Documentation -> Validate configuration

If you are getting cloudformation template validation errors, I recommend attempting to upload the cloudformation manually, it will fail at the
first step if it isn't validated properly. Once it passes the upload step, you know that it is validated properly
and you can deploy it through riff-raff.

Sometimes riff-raff can be a bit flakey creating the Cloudformation stack on the first deploy. If you are having trouble, upload it manually
first then try deploying through riff-raff. If you do upload it manually, make sure you add the following tags when
prompted:

Stack: `playground`
Stage: `PROD`
APP: `<name of project>`

24. You should now be able to find your Lambda by navigating to the Lambda section of the console and searching for your lambda.

If you click Test, it should run successfully and return the payload "hello world"

Now for week 2 https://github.com/guardian/jr-api-gateway-workshop/blob/master/TutorialWeek2.md
