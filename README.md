# Serverless Bank Transfer

A rudimentary implementation of a bank transferring system, built using lambda functions and the serverless framework.

## Requirements

Java == 8

Scala >= 1.11

[serverless framework](https://serverless.com/framework/docs/getting-started/)

[AWS credentials](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html) for an IAM user that can create lambdas and dynamodb tables.

## Usage
```
cd serverless-bank-transfer
sbt assembly
serverless deploy
```
