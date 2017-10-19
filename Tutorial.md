# jr-api-gateway-workshop


1. Go to https://repo-genesis.herokuapp.com/ and create a repo.

2. Set up the repo locally and make your first commit

3. Add a gitignore file to the root (see https://github.com/guardian/jr-api-gateway-workshop/blob/master/.gitignore)

4. Create a file called build.sbt in the root (https://github.com/guardian/jr-api-gateway-workshop/blob/jr-tc/build.sbt)

5. Create a file called plugins.sbt in a new directory “project”

6. Add the following 2 lines : 
`addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0")
	addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.0.0")`

7. Compile using `sbt compile`
 in the root, create new folder src/main/scala/com.gu/microserviceWorkshop

8. Create file Lambda.scala (https://github.com/guardian/jr-api-gateway-workshop/blob/master/src/main/scala/com/gu/microserviceWorkshop/Lambda.scala). The ‘handler’ function is the function that will be called when your lambda is invoked. You can name it whatever you want, but handler will do!  

9. Push to github
 Go to https://teamcity.gutools.co.uk/admin/editProject.html?projectId=Playground

10. Create subproject, give it a unique name

11. Add a 'simple build tool (scala)' build step "

12. Go to “Edit build configuration”

13. Edit “sbt command” to: clean compile test riffRaffUpload

14. Go to VCS roots, choose the root

15. Show advanced options

16. Set branch specifications to +:refs/heads/* (this allows all your branches from github to automatically build)

17. Check that branches are building by pushing to a new branch

18. Create a riff-raff.yaml and add the following: 

```stacks: [playground]
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
      actions: [uploadLambda]```

18. Push, wait for build on TC, go to https://riffraff.gutools.co.uk/deployment/request and start typing in your project name. Click deploy
