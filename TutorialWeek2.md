22. Now for the API gateway. First, add the following Rest Api resource to your cloudformation.
 It should be at the same level as your Lambda resource

 ```
 WorkshopApi:
  Type: AWS::ApiGateway::RestApi
  Properties:
    Description: Api to call our lambda
    Name: !Sub ${App}-api-${Stage}
```
This resource will later contain a collection of Amazon API Gateway resources and methods
that can be invoked through HTTPS endpoints. (

For more on AWS::ApiGateway::RestApi see http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-apigateway-restapi.html

23. We now need to add a resource and associated method that will be invoked. Add the following
to your template:

```
WorkshopApiResource:
  Type: AWS::ApiGateway::Resource
  Properties:
      RestApiId: !Ref WorkshopApi
      ParentId: !GetAtt [WorkshopApi, RootResourceId]
      PathPart: helloWorld
  DependsOn: WorkshopApi
```

A few new things here. `DependsOn` states that before this resource is created,
the WorkshopApi must be created first.

`!GetAtt [WorkshopApi, RootResourceId]` get the `RootResource` attribute of `WorkshopApi`

The `PathPart` defined the path of the url that you will call to invoke your api, i.e
here we will call 'https://...../helloWorld' to invoke the API from a REST client.

For more on AWS::ApiGateway::Resource, see http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-apigateway-resource.html

24. Next we add a method on the resource, which defines the parameters and body that clients
 must send in their requests. Add the following to your cloudformation:

 ```
 WorkshopApitMethod:
  Type: AWS::ApiGateway::Method
  Properties:
    AuthorizationType: NONE
    RestApiId: !Ref WorkshopApi
    ResourceId: !Ref WorkshopApiResource
    HttpMethod: POST
    Integration:
      Type: AWS_PROXY
      IntegrationHttpMethod: POST
      Uri: !Sub arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${WorkshopLambda.Arn}/invocations
  DependsOn:
  - WorkshopApi
  - WorkshopLambda
  - WorkshopApiResource
```

A few new things here again. The `Integration` describes the target backend that the API
will call, in our case, our Lambda. The type we have chosen, `AWS_PROXY`, was designed
specifically for integrating a method request with a Lambda function in the backend.
 With this integration type, API Gateway applies a default mapping template to send the
entire request to the Lambda function and transforms the output from the Lambda function
to HTTP responses, which makes it perfect for use with API Gateway.

For more, see http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-set-up-simple-proxy.html

25. Now we need to define a deployment for our resource, which will deploy our API to a stage
so that our client cal call the API over the internet. Add the following to your template:

```
WorkshopApiAPIDeployment:
  Type: AWS::ApiGateway::Deployment
  Properties:
    Description: Deploys the API into an environment/stage
    RestApiId: !Ref WorkshopApi
  DependsOn: WorkshopApiMethod
```


26. Next we need to add the stage that the API will deploy to. Add the following to your template:

```
WorkshopApiStageProd:
   Type: AWS::ApiGateway::Stage
   Properties:
     StageName: !Ref Stage
     Description: Prod Stage
     RestApiId: !Ref WorkshopApi
     DeploymentId: !Ref WorkshopApiDeployment
     Variables:
       Stack: !Ref Stack
     MethodSettings:
       - ResourcePath: /helloWorld
         HttpMethod: POST
         MetricsEnabled: 'true'
         DataTraceEnabled: 'true'
         ThrottlingBurstLimit: '999'
   DependsOn:
   - WorkshopApi
   - WorkshopApiDeployment
   ```

   Make sure that your path matches up with what you set as `PathPart` in the resource


27. Now we have an API set up, we need give our IAM role permission to invoke it. Add the following to
the `Policies` in your IAM role (the first resource we added to the template) :


```
- PolicyName: api
  PolicyDocument:
    Statement:
      Effect: Allow
      Action:
        - execute-api:Invoke
      Resource: "*"
```

28. Push, wait for teamcity to build then deploy your project. This should create your API Gateway.
Navigate to API gateway in the console and search for your API. Once you have found it, click on `Resources`
and then `Post`, under `/helloWorld`. You should be able to test it. It will fail! If you look at the Logs,
you should see 'Malformed Lambda proxy response' at the bottom. That is because AWS_PROXY requires that
the response is in the form :
```
{
  statusCode: Int,
  headers: Map[String, String]
  body: String
}
```
To do this, we will create a case class which we will serialize into json using the `circe` library.
First, add

```
"io.circe" %% "circe-parser" % "0.7.0",
"io.circe" %% "circe-generic-extras_sjs0.6" % "0.7.0"
  exclude("org.typelevel", "cats-core_sjs0.6_2.11" )
  exclude("com.chuusai", "shapeless_sjs0.6_2.11")
```

to your libraryDependenceis in your `build.sbt`

Then create a new scala class called ApiResponse, with the following code:

```
package com.gu.microserviceWorkshop

import io.circe.Encoder
import io.circe.syntax._
import io.circe.generic.extras.semiauto._


case class APIResponse(statusCode: Int, headers: Map[String, String], body: String)

object APIResponse {

  private val stringAPIResponseEncoder : Encoder[APIResponse] = deriveEncoder

  //needed as API gateway requires that the body is returned as a string
  implicit def responseEncoder: Encoder[APIResponse] = Encoder.instance { responseA =>
    responseA.copy(body = responseA.body.asJson.noSpaces).asJson(stringAPIResponseEncoder)
  }

  def fromResult(body: String): APIResponse = {
      APIResponse(200,  Map("Content-Type" -> "application/json"), body)
  }

}
```

and change your handler function in Lambda.scala to :

```
def handler(in: InputStream, out: OutputStream): Unit = {

  val response = APIResponse(200,  Map("Content-Type" -> "application/json"), "hello world again")

  //no spaces converts json to a string
  out.write(response.asJson.noSpaces.getBytes(UTF_8))

}
```

You will also need to add the following imports:

```
import io.circe.syntax._
import java.nio.charset.StandardCharsets.UTF_8
```

Push, build and deploy, and your API should work!
