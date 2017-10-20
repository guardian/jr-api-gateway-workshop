# jr-api-gateway-workshop


1. Go to https://repo-genesis.herokuapp.com/ and create a repo with what will now be referrerd to as `<name of project>`. An example name of project is jr-microservice workshop, you will want to call it something unique so use your initials

2. Set up the repo locally and make your first commit

3. Add a gitignore file to the root (see https://github.com/guardian/jr-api-gateway-workshop/blob/master/.gitignore)

4. Create a file called build.sbt in the root with the following: 

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

15. On the "New Build Step" page that comes up, edit “Sbt commands” to: `clean compile test riffRaffUpload` 
and click save

16. Go to Version Control Settings in the side menu and click edit on the VCS root that appears

17. Show advanced options

18. Set branch specifications to `+:refs/heads/*` (this allows all your branches from github to automatically build)
and save

19. Check that branches are building by pushing to a new branch and navigating to your build through the Projects 
in the toolbar (your project will be under Playground)

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
You can check your cloudformation template is valid at any point using the aws cli cloudformation validation-template command: 
``` 
 aws cloudformation validate-template --template-body file:///Path/to/you/file/cfn.yaml
```
Make sure you replace <name of project> with your project name you decided on in step 1

21. Push, wait for build on TC, and go to https://riffraff.gutools.co.uk/deployment/request and 
start typing in your project name. Click deploy [NOTE: if it doesn't show up, riff raff may need redeploying]
 This will upload your artifact to
 `https://s3.console.aws.amazon.com/s3/buckets/gu-jr-microservice-workshop-dist/playground/PROD/<name of project>/<name of project>.zip`

22. Create a cloudformation template, cfn.yaml, in the root with the following:

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
      
  WorkshopLambdaInvokePermission:
    Type: AWS::Lambda::Permission
    Properties:
      Action: lambda:InvokeFunction
      FunctionName: !Sub ${App}-${Stage}
      Principal: apigateway.amazonaws.com
    DependsOn: WorkshopLambda

```

Don't forget to replace `<name of your project>`.
Here we have defined 2 resources: An execution roll that is given permission to invoke the Lambda,
and the Lambda itself. You will see that under 'Code', we have told the template where to find our
code, in the bucket defined in the DeployBucket parameter at the top of the template (we use `Ref` to
refer to a single parameter), in this case gu-jr-microservice-workshop-dist, and the key (which is
basically the path to the file and the filename).which in our case will be `playground/PROD/<name of 
your project>/<name of your project>.zip`. `!Sub` is used to refer to multiple parameters in the parameters 
section defined at the top in one go.

23. Add the following deployments to your riff-raff.yaml (Don't forget to replace `<name of project>`)
```
<name of project>-cfn:
  type: cloud-formation
  app: <name of your project>
  parameters:
    prependStackToCloudFormationStackName: false
    cloudFormationStackName: <name of project>
    templatePath: cfn.yaml
  dependencies: [<name of project>-upload]
<name of project>-lambda-update:
  template: <name of project>
  actions: [updateLambda]
  dependencies: [<name of project>-cfn]
```

The first one is of type 'cloudformation', and what this does is tells riff-raff to upload or update
the cloudformation stack with the Stack, Stage and App specified in the parameters section of the cloudformation
template.

The second one, which has the action updateLambda, updates the lambda with the .zip file that is in your S3 bucket. 

If you are having trouble knowing if your riff-raff template is formatted correctly, try going to https://riffraff.gutools.co.uk/
, then navigating to Documentation -> Validate configuration

24. You should now be able to find your Lambda by navigating to the Lambda section of the console and
searching for your lambda. 

If you click Test, it should run successfully and return the payload "hello world"

Now for week 2 https://github.com/guardian/jr-api-gateway-workshop/blob/master/TutorialWeek2.md	

