# AWS_POC
Basic AWS POC using S3, AWSLambda, SQS and DynamoDB

Prerequisites:
To build and run these project, you'll need:
Maven (>3.6.0)
AWS SDK for Java (downloaded and extracted somewhere on your machine)
AWS credentials, either configured in a local AWS credentials file or by setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.
You should also set the AWS region within which the operations will be performed. If a region is not set, the default region used will be ap-south-1.

Building the project:
Go to Test folder
cd /Test

execute the below maven command
Have used AWS Plugins to run the project

A I am new to Gradle have built this project using Maven.

 
Before executing the AWS commands, user should be able to create IAM new role with example name 'SQS_DynamoDB' by adding the following Inline polocies using AWS management console
 AmazonSQSFullAccess
 AmazonDynamoDBFullAccess
 AWSLambdaExecute
 CloudWatchEventsFullAccess
 
1)Configure aws with based on your account 
D:\AWS\assignment>aws configure
AWS Access Key ID [****************AN4S]: YOUR ACCESS KEY
AWS Secret Access Key [************************************Sc21]: YOUR SECRET KEY
Default region name [None]: ap-south-1
Default output format [None]: json 
 
Once the role gets created, user can able to see Role ARN
arn:aws:iam::214340667153:role/{SQS_DynamoDB}
 
 
2)**Using Amazon DynamoDB with the AWS CLI**
Command::
aws dynamodb create-table --table-name Combinations  --attribute-definitions AttributeName=MessageId,AttributeType=S  --key-schema AttributeName=MessageId,KeyType=HASH  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

Ex:
D:\AWS\assignment>aws dynamodb create-table --table-name Combinations  --attribute-definitions AttributeName=MessageId,AttributeType=S  --key-schema AttributeName=MessageId,KeyType=HASH  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

OUTPUT:
{
    "TableDescription": {
        "AttributeDefinitions": [
            {
                "AttributeName": "MessageId",
                "AttributeType": "S"
            }
        ],
        "TableName": "Combinations",
        "KeySchema": [
            {
                "AttributeName": "MessageId",
                "KeyType": "HASH"
            }
        ],
        "TableStatus": "CREATING",
        "CreationDateTime": "2020-06-03T09:15:18.081000+05:30",
	"ProvisionedThroughput": {
            "NumberOfDecreasesToday": 0,
            "ReadCapacityUnits": 5,
            "WriteCapacityUnits": 5
        },
        "TableSizeBytes": 0,
        "ItemCount": 0,
        "TableArn": "arn:aws:dynamodb:ap-south-1:214340667153:table/Combinations"
    }
}

3) **To create a queue**
This example creates a queue with the specified name, sets the message retention period to 3 days (3 days * 24 hours * 60 minutes * 60 seconds), and sets the queue's dead letter queue to the specified queue with a maximum receive count of 1,000 messages.
Command::
aws sqs create-queue --queue-name CombineMatchQueue	

Ex:
D:\AWS\assignment>aws sqs create-queue --queue-name CombineMatchQueue

OUTPUT:
{
    "QueueUrl": "https://sqs.ap-south-1.amazonaws.com/214340667153/CombineMatchQueue"
}

4)**To create a Lambda function**
The following ``create-function`` example creates a Lambda function named ``AWS``. ::
Command::
    aws lambda create-function  --function-name AWS  --runtime Java 8   --zip-file fileb://AWS.zip   --handler com.amazonaws.aws.test.LambdaFunctionHandler::handleRequest  --role arn:aws:iam::214340667153:role/SQS_DynamoDB

**To create a mapping between an event source and an AWS Lambda function**
The following ``create-event-source-mapping`` example creates a mapping between an SQS queue and the ``AWS`` Lambda function. ::
Command::
    
D:\AWS\assignment>aws lambda create-event-source-mapping  --function-name AWS  --batch-size 5  --event-source-arn arn:aws:sqs:ap-south-1:214340667153:CombineMatchQueue
{
    "UUID": "6ddfe0e8-e88c-44db-97e8-fba7f15dcd0c",
    "BatchSize": 5,
    "EventSourceArn": "arn:aws:sqs:ap-south-1:214340667153:CombineMatchQueue",
    "FunctionArn": "arn:aws:lambda:ap-south-1:214340667153:function:AWS",
    "LastModified": "2020-06-05T17:10:02.095000+05:30",
    "State": "Creating",
    "StateTransitionReason": "USER_INITIATED"
}

# The Link to the Video is : https://teams.microsoft.com/_#/my/file-personal?context=AWS%2520POC&rootfolder=%252Fpersonal%252Fkaran_shah_theaa_com%252FDocuments%252FAWS%2520POC
	
	
