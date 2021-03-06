# Congratulations!
You made it to week 3. So far, this workshop has been a "follow the steps" kind of exercise, so for this week, we are 
going to try have a go by ourselves. 

# Part 1
Currently, your service doesn't take any information, it just spits out `"hello world"`, regardless of the input.  Usually when 
making a microservice, we would want to return some variable output, depending on the input. We are going to make an API 
that takes a number as input and if the number is a prime number, returns the string `"this is a prime number"`, otherwise
returns `"this is not a prime number"`.

Hint: you can use this boilerplate to deal with reading the input and returning output (this example assumes result is already a string): `jsonPayload` will just be the JSON that is passed to the handler

```
  def handler(in: InputStream, out: OutputStream): Unit = {
    val jsonPayload = scala.io.Source.fromInputStream(in).mkString("")

    val result = ???

    out.write(result.getBytes(UTF_8))
  }
 ```
# Part 2
Currently, our API is open to the whole world. This means that anyone with the link could spam our service, or be able to 
use our secret prime number identification service whenever they like. This could be a problem. One way we can fix this
is buy adding an API key to the service. 

I will not upload the steps for you this time, but here are some hints:

. You will need to add a `AWS::ApiGateway::UsagePlan`, a `AWS::ApiGateway::ApiKey` and a `AWS::ApiGateway::UsagePlanKey` to your cloudformation template. Look them up in the AWS docs 

. You will have to add a property to your `AWS::ApiGateway::Method` that sets the API key required property. 

. Once you succesfully deploy your project with the API key parts implemented, you may have to manually redeploy your API Stage in the console to update the API to require an API key.

. You can see how to use Postman to call your API with an API key here http://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-use-postman-to-call-api.html

If you get stuck, you can cheat and have a look at this PR.
https://github.com/guardian/jr-test-workshop/pull/1/files

# Bonus challenge
Your API currently returns a single line in the body, but often you would want to return a JSON object with multiple 
attributes. For this challenge, we will return an Object that looks like this:

```
{
  number: <the number we want to return>,
  isPrime: <true or false>
}
```

Hint: create a case class for your result data structure, and make a compainion object with an implicit encoder that will let you call `.asJson` on the result. 

# Extras 
If you have got this far and still want to do more, there are other things we havn't covered yet that you might want to have a go at

. Have a go at setting up continuous deployment, so that master deploys automatically


# Stuck?

Here is one I made earlier https://github.com/guardian/jr-test-workshop
