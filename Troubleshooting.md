Known problems and answers:

1. I'm getting an error like this:
```
[16:32:24] playground-PROD-lambda-test(AWS::CloudFormation::Stack}: UPDATE_ROLLBACK_IN_PROGRESS The following resource(s) failed to create: [WorkshopLambda].
```

or something to do with ROLLBACK

*Answer: You need to manually delete the stack in the Cloudformation console. AWS is stupid if you try and upload a CF template and it doesn't have a working template to fall back on, it enters this weird state that isn't useful for anyone*
