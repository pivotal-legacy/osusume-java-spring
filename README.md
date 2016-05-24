# osusume-java-spring
Osusume back-end built in Java Spring Boot.

## Configuration
A environment variable for the database URL needs to be configured for PostgreSQL, such as:

`OSUSUME_DATABASE_URL=jdbc:postgresql://pivotal:@localhost/osusume-test`

## Makefile
Please use the makefile which contains a few useful commands:

**refreshdb** Refreshes the database scheme. Run this to setup the DB for the first time or to wipe out all data.

**loadsampledata** Loads the base set of sample data that can be used with the application.

**tests** Runs all tests and then loads all sample data. ***Please note that this will remove all existing data and re-load the sample data!***

## Starting the Server
The project is broken down into an "application" and a "components" directory, therefore to start the server please locate the jar under the 'applications' directory:

`java -jar applications/api/build/libs/osusume-java-spring-0.0.1-SNAPSHOT.jar`
