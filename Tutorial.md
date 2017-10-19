# jr-api-gateway-workshop


Go to https://repo-genesis.herokuapp.com/ and create a repo.

Set up the repo locally and make your first commit

Add a gitignore file to the root (see https://github.com/guardian/jr-api-gateway-workshop/blob/master/.gitignore)

create a file called build.sbt in the root (https://github.com/guardian/jr-api-gateway-workshop/blob/jr-tc/build.sbt)

Create a file called plugins.sbt in a new directory “project”

add the following 2 lines : 
`addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0")
	addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.0.0")`

Compile using `sbt compile`
 in the root, create new folder src/main/scala/com.gu/microserviceWorkshop

Create file Lambda.scala (https://github.com/guardian/jr-api-gateway-workshop/blob/master/src/main/scala/com/gu/microserviceWorkshop/Lambda.scala). The ‘handler’ function is the function that will be called when your lambda is invoked. You can name it whatever you want, but handler will do!  

Push to github
 Go to https://teamcity.gutools.co.uk/admin/editProject.html?projectId=Playground

Create subproject, give it a unique name

Add a 'simple build tool (scala)' build step "

Go to “Edit build configuration”

Edit “sbt command” to: clean compile test riffRaffUpload

go to VCS roots, choose the root

show advanced options

set branch specifications to +:refs/heads/* (this allows all your branches from github to automatically build)

check that branches are building by pushing to a new branch
 Create a riff-raff.yaml (https://github.com/guardian/jr-api-gateway-workshop/blob/master/riff-raff.yaml)
 Keep bucket name, region, stack. Change ‘jr-microservice-workshop’ to the name of your project. You can rename “jr-microservice-upload”, “jr-microservice-workshop-cfn”, "jr-microservice-lambda-update” if you wish. 

Push, wait for build on TC, go to https://riffraff.gutools.co.uk/deployment/request and start typing in your project name. Click deploy, then go...but it won’t work yet! The first deployment in riff-raff.yaml takes your teamcity build and puts it in the right place, which will work. But then we are telling riff-raff to upload a cloudformation template that we havent made yet (the one with type : cloud-formation), and telling a lambda that doesn’t exist yet to use our code. We can make it work by clicking “Preview” rather than “Deploy”, and deselecting the last 2 steps in the deployment. If this works, you should be able to see your zipped build in S3  (gu-jr-microservice-workshop-dist/playground/PROD/<name of project>/<name of project.zip>)

