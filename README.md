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

## Test cases

TODO: Implement the following integration test cases:

Set-up for each of the following scenarios:

Balances table:
Alice $100
Bob $50

### Scenario 1 (insufficient balance for executed transfer)

#1: $100 Alice -> Bob now (success)

#2: $1 Alice -> Bob now (failure -- insufficient balance)


### Scenario 2 (insufficient balance for scheduled transfer)

#1: $100 Alice -> Bob in 1 hour (success)

#2: $1 Alice -> Bob now (failure -- insufficient balance)


### Scenario 3 (cancelling frees up available balance)

#1: $50 Bob -> Alice in 1 minute (success)

#2: $1 Bob -> Alice now (failure -- insufficient balance)

cancel transfer #1 (success)

#3: $50 Bob -> Alice now (success)


### Scenario 4 (cancelling executed transaction fails)

#1: $1 Alice -> Bob now (success)

cancel transfer #1 (fails -- already executed)

### Scenario 5 (same transfer ID re-sent)

#1: $1 Alice -> Bob now (success)

#2: Resend transfer #1 (failure -- transfer schedule request already processed)
