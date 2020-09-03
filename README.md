# transactions-service

Transaction Service is a microservice for transactions between bank accounts.

### Solution

Two database tables has been created **account** and **transaction**.

In account database is stored iban and balance of accounts (and creation/cancellation dates).
Integer is used as type of primary key.

In transaction database store transactions between accounts. Each transaction is 
composed by a reference, amount, fee and description. Two more fields are needed
to reference the accounts between the transaction has been made.

Some assumptions have been made about fields:
- balance: Balance limit established in 999,999,999.99
- amount: amount to transfer can't be higher than 99,999.99, but can be higher 
with fee.
- Can't do transactions between the same account.
- Formula to calculate total to transfer is amount(1-0.001*fee).

Database is initialized at start application.

The schema of the database es in schema.sql. data.sql inserts three accounts
when the application starts.

According to this domain model I have created two services:

**AccountService** which have two methods: 
- _withdrawMoney_: substract money from account.
- _transferMoney_: add money to an account.

**TransactionService** which have three methods:

- _createTransaction_: creates a transaction between two accounts.
- _transactionsFromAccount_: lists all transactions done by an account filtered
by iban and sorted by amount.
- _checkStatus_: Checks the status of a transaction providing a reference and
a channel.

The endpoints created are those defined in the practice statement.

### Technology stack

It's developed in Java 8 (JDK 1.8.0_265) with Spring Boot 2.3.3-RELEASE.

Starters used are: Web, data jpa, validation, test.
Database used is embedded H2.
It is a must to have Maven 3.X.X installed.

To compile run the command:

```shell script
mvn clean install
```

To run the tests only
```shell script
mvn test
``` 

To run the project open a terminal and execute the command:
```shell script 
mvn spring-boot:run
```

Once started the project we can access to Open API 3.0 interface.
Open a browser and type this in the address bar:

```
http://localhost:8080/swagger-ui.html
```

H2 console is enabled by default so we can access too.
To access H2 console type in the address bar

```
http://localhost:8080/h2
```

## REMARK

While I was developing the practice I used SNAKE_CASE configuration for properties but\
when I included open api library for documentation, swagger ui avoid this configuration.
So I have disabled the property **property-naming-strategy** in application.yml.

If you want to use snake case you need to uncomment this line in application.yml.