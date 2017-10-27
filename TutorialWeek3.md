# Congratulations!
You made it to week 3. So far, this workshop has been a "follow the steps" kind of exercise, so for the final week, we are 
going to try have a go by ourselves. 

# Part 1
Currently, your service doesn't take any information, it just spits out `"hello world"`, regardless of the input.  Usually when 
making a microservice, we would want to return some variable output, depending on the input. We are going to make an API 
that takes a number as input and if the number is a prime number, returns the string `"this is a prime number"`, otherwise
returns `"this is not a prime number"`.

# Part 2
Currently, our API is open to the whole world. This means that anyone with the link could spam our service, or be able to 
use our secret prime number identification service whenever they like. This could be a problem. One way we can fix this
is buy adding an API key to the service. 

I will not upload the steps for you this time, but here are some hints:

. You will need to add a `AWS::ApiGateway::UsagePlan`, a `AWS::ApiGateway::ApiKey` and a `AWS::ApiGateway::UsagePlanKey` to your cloudformation template. Look them up in the AWS docs 

. You will have to add a property to your `AWS::ApiGateway::Method` that sets the API key required property. 

. Once you succesfully deploy your project with the API key parts implemented, you may have to manually redeploy your API Stage in the console to update the API to require an API key.

. You can see how to use Postman to call your API with an API key here http://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-use-postman-to-call-api.html
