Known problems and answers:

1. I'm getting an error like this:
    ```
    [16:32:24] playground-PROD-lambda-test(AWS::CloudFormation::Stack}: UPDATE_ROLLBACK_IN_PROGRESS The following resource(s) failed to create: [WorkshopLambda].
    ```

    or something to do with ROLLBACK

    __Answer: You need to manually delete the stack in the Cloudformation console. AWS is stupid: if you try and upload a CF template and it doesn't have a working template to fall back on, it enters this weird state that isn't useful for anyone. To delete it, go to janus and click on the cloud icon for the Dev Playground account. Make sure you are in the right region (top right, it should say Ireland). Then navigate to CloudFormation in services. You should see your stack, with ROLLBACK_COMPLETE next to it in red. Delete the stack! You may wish to validate the template locally before carrying on (see q3)__




2. My riff-raff template is getting validation errors. Is there some way I can validate it before going through the process of building and attempting to deploy?
    __Answer: Try going to https://riffraff.gutools.co.uk/ , then navigating to Documentation -> Validate configuration, and pasting in your configuration__


3. My cloudformation template is invalid and I don't want to wait until it has built in teamcity to find out!
__Answer: First, go to the Janus credentials page where you usually copy and paste your AWS credentials from, and copy and paste the creds in the EXPORT TO SHELL section. Then run `aws cloudformation validate-template --template-body file:///<path to file>` and you will be able to test the vailidity of your template locally__
