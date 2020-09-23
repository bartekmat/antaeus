# Candidate : Bartosz Matuszewski

Date : 23.09.2020

## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

---

# The Solution
## Time spent

I worked on my solution heavily on the span of 4 days. I estimate the total number of hours spent reading documentation, doing research, writting code and testing to 40-45. This was one of my first chances to play around with Kotlin - I had lots of fun with it, but I am aware that many of its specific advantages were probably not used to the extent they could have been :) 

## Initial assumptions

 - The entire activity when all pending invoices are attempted to be charged has to be automatic. Therefore an kind of task scheduler is needed. 
 - There are few different problems that can occur. What we do with them is buisness decision, however I think I should presented en example of how could they be handled. Therefore, networkException will cause the app to try again set number of times before giving up and passing to external handler. In case of currencyMismatch the app will attempt to correct the invoice. If the customer on the invoice does not exist there is little to be done really. I left the space to pass the invoice to some external rapport generator etc.
 
# Issues to solve

There are countless for sure :) In this solution I decided to focus on:

## How often is 1 day of month?

It depends! After doing some research I decided that my AntaeausTaskScheduler should depend on orq.Quartz library. It allows me to schedule the call of billingService.proceedAllPendingInvoices() monthly. Scheduler is set in AntaeusApp.kt where everything else is initialized.

Limitations.  
Scheduler i used from Quartz is a default one. They way it can be replaced/configured to for example: handle TimeZones is yet untackled. For now all invoices are charged on the server local date. I do not think it is desirable situation bearing in mind Pleo's clients around the world. We would need customer's time zone or at least country stored in database. 

## How to correct wrong currency on the invoice?

I decided to go with sending the call to some external currency API to convert the amout, then kill the bad invoice and create correct copy and charging it immidiately (whether it should be done immidiately is a matter of business decision again)

Limitations.  
Ideally I should hit an endpoint that takes two currencies and value and does the conversion for me. In most trustworthy currency api providers such an endpoint is only for commerial use. That is why I parse the big json with all currencies.

# Probles left unsolved for now.

Invoices are proceeded one by one. Ideally when fetched from the database, they should be placed in some queue and taken from it by several working threads. I read that similar effect can by achieved using Coroutines. That would be the way to go!





---
below I leave the description I got.


## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!
