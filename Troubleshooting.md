Known problems and answers:

1. I'm getting an error like this:
```
[16:32:24] playground-PROD-lambda-test(AWS::CloudFormation::Stack}: UPDATE_ROLLBACK_IN_PROGRESS The following resource(s) failed to create: [WorkshopLambda].
```

or something to do with ROLLBACK

__Answer: You need to manually delete the stack in the Cloudformation console. AWS is stupid: if you try and upload a CF template and it doesn't have a working template to fall back on, it enters this weird state that isn't useful for anyone__


2. My riff-raff template isn't getting validation errrors. Is there some way I can validate it before going through the process of building and attempting to deploy?

__Answer: Try going to https://riffraff.gutools.co.uk/ , then navigating to Documentation -> Validate configuration, and pasting in your configuration
